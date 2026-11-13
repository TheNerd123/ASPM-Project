package com.teamvoid.aspm.api;

import com.teamvoid.aspm.domain.Application;
import com.teamvoid.aspm.domain.Finding;
import com.teamvoid.aspm.domain.Scan;
import com.teamvoid.aspm.domain.Scanner;
import com.teamvoid.aspm.service.AspmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AspmController {

    private final AspmService service;

    public AspmController(AspmService service) {
        this.service = service;
    }

    @GetMapping({"/applications", "/v1/applications"})
    public List<Application> applications() {
        return service.applications();
    }

    @PostMapping({"/applications", "/v1/applications"})
    @ResponseStatus(HttpStatus.CREATED)
    public Application createApplication(@Valid @RequestBody CreateApplicationRequest request) {
        return service.createApplication(request);
    }

    @PatchMapping({"/applications/{applicationId}/deactivate", "/v1/applications/{applicationId}/deactivate"})
    public Application deactivateApplication(@PathVariable UUID applicationId) {
        return service.deactivateApplication(applicationId);
    }

    @GetMapping({"/scanners", "/v1/scanners"})
    public List<Scanner> scanners() {
        return service.scanners();
    }

    @GetMapping({"/scans", "/v1/scans"})
    public List<Scan> scans() {
        return service.scans();
    }

    @PostMapping({"/scans", "/v1/scans"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Scan createScan(@Valid @RequestBody CreateScanRequest request) {
        return service.createScan(request);
    }

    @GetMapping({"/findings", "/v1/findings"})
    public List<Finding> findings() {
        return service.findings();
    }

    @GetMapping({"/dashboard/summary", "/v1/dashboard/summary"})
    public DashboardSummary dashboardSummary() {
        return service.dashboardSummary();
    }
}
