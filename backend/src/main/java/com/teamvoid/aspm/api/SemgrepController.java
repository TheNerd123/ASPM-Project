package com.teamvoid.aspm.api;

import com.teamvoid.aspm.service.SemgrepScanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SemgrepController {

    private final SemgrepScanService semgrepScanService;

    public SemgrepController(SemgrepScanService semgrepScanService) {
        this.semgrepScanService = semgrepScanService;
    }

    @PostMapping({
            "/semgrep/scans", "/v1/semgrep/scans",
            "/scans/semgrep", "/v1/scans/semgrep"
    })
    public SemgrepScanResponse scan(@Valid @RequestBody SemgrepScanRequest request) {
        return semgrepScanService.scan(request);
    }
}
