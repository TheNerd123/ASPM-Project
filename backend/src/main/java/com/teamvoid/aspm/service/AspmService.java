package com.teamvoid.aspm.service;

import com.teamvoid.aspm.api.CreateApplicationRequest;
import com.teamvoid.aspm.api.UpdateApplicationRequest;
import com.teamvoid.aspm.api.CreateScanRequest;
import com.teamvoid.aspm.api.DashboardSummary;
import com.teamvoid.aspm.domain.Application;
import com.teamvoid.aspm.domain.AssetStatus;
import com.teamvoid.aspm.domain.Finding;
import com.teamvoid.aspm.domain.FindingStatus;
import com.teamvoid.aspm.domain.Scan;
import com.teamvoid.aspm.domain.ScanStatus;
import com.teamvoid.aspm.domain.Scanner;
import com.teamvoid.aspm.domain.Severity;
import com.teamvoid.aspm.persistence.ApplicationEntity;
import com.teamvoid.aspm.repository.ApplicationRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AspmService {

    private final ApplicationRepository applicationRepository;
    private final ConcurrentMap<UUID, Scanner> scanners = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Scan> scans = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Finding> findings = new ConcurrentHashMap<>();

    public AspmService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
        Scanner semgrep = new Scanner(UUID.randomUUID(), "Semgrep", "SAST", true);
        scanners.put(semgrep.id(), semgrep);

        //Temporary solution, may need to be improved later on
        Application payments = applicationRepository.findAll().stream()
            .filter(entity -> "Payments API".equals(entity.getName()) && "https://github.com/teamvoid/payments-api".equals(entity.getRepositoryUrl()))
            .findFirst()
            .map(this::toDomain)
            .orElseGet(() -> createApplication(
                new CreateApplicationRequest(
                    "Payments API",
                    "https://github.com/teamvoid/payments-api"
                )
        ));
        
        Scan scan = new Scan(UUID.randomUUID(), payments.id(), semgrep.id(), ScanStatus.COMPLETED, 18, Instant.now());
        scans.put(scan.id(), scan);
        Finding credentialFinding = new Finding(UUID.randomUUID(), payments.id(), Severity.CRITICAL, "Hard-coded credential", FindingStatus.OPEN, Instant.now());
        Finding dependencyFinding = new Finding(UUID.randomUUID(), payments.id(), Severity.HIGH, "Unsafe dependency version", FindingStatus.OPEN, Instant.now());
        findings.put(credentialFinding.id(), credentialFinding);
        findings.put(dependencyFinding.id(), dependencyFinding);
    }

    public List<Application> applications() {
        return applicationRepository.findAll().stream().map(this::toDomain).sorted(Comparator.comparing(Application::createdAt).reversed()).toList();
    }

    public Application deactivateApplication(UUID applicationId) {
        ApplicationEntity entity = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("application", applicationId));

        entity.setStatus(AssetStatus.INACTIVE);

        ApplicationEntity savedEntity = applicationRepository.save(entity);

        return toDomain(savedEntity);
    }

    public Application createApplication(CreateApplicationRequest request) {
        ApplicationEntity entity = new ApplicationEntity(UUID.randomUUID(), request.name().trim(), request.repositoryUrl(), AssetStatus.ACTIVE, Instant.now());
        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return toDomain(savedEntity);
    }

    public Application updateApplication(UUID applicationId, UpdateApplicationRequest request) {
        ApplicationEntity entity = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("application", applicationId));

        entity.setName(request.name().trim());
        entity.setRepositoryUrl(request.repositoryUrl());

        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return toDomain(savedEntity);
    }

    private Application toDomain(ApplicationEntity entity) {
        return new Application(entity.getId(), entity.getName(), entity.getRepositoryUrl(), entity.getStatus(),entity.getCreatedAt());
    }

    public List<Scanner> scanners() {
        return scanners.values().stream().sorted(Comparator.comparing(Scanner::name)).toList();
    }

    public List<Scan> scans() {
        return scans.values().stream().sorted(Comparator.comparing(Scan::startedAt).reversed()).toList();
    }

    public Scan createScan(CreateScanRequest request) {
        Application application = requireApplication(request.applicationId());
        if (application.status() != AssetStatus.ACTIVE) {
            throw new InactiveApplicationException(application.id());
        }
        if (!scanners.containsKey(request.scannerId())) {
            throw new ResourceNotFoundException("scanner", request.scannerId());
        }
        Scan scan = new Scan(UUID.randomUUID(), request.applicationId(), request.scannerId(), ScanStatus.QUEUED, 0, Instant.now());
        scans.put(scan.id(), scan);
        return scan;
    }

    public List<Finding> findings() {
        return findings.values().stream().sorted(Comparator.comparing(Finding::createdAt).reversed()).toList();
    }

    public DashboardSummary dashboardSummary() {
        List<Application> allApplications = applications();
        long open = findings.values().stream().filter(f -> f.status() == FindingStatus.OPEN).count();
        return new DashboardSummary(
                allApplications.size(),
                allApplications.stream().filter(a -> a.status() == AssetStatus.ACTIVE).count(),
                allApplications.stream().filter(a -> a.status() == AssetStatus.INACTIVE).count(),
                open,
                countOpen(Severity.CRITICAL),
                countOpen(Severity.HIGH),
                countOpen(Severity.MEDIUM),
                countOpen(Severity.LOW)
        );
    }

    private long countOpen(Severity severity) {
        return findings.values().stream().filter(f -> f.status() == FindingStatus.OPEN && f.severity() == severity).count();
    }

    private Application requireApplication(UUID id) {
        return applicationRepository.findById(id)
                .map(this::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("application", id));
    }
}
