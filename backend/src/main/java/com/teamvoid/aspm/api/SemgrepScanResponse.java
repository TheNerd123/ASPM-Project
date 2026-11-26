package com.teamvoid.aspm.api;

import java.util.List;
import java.util.UUID;

/** API response for a completed Semgrep scan. */
public record SemgrepScanResponse(
        UUID scanId,
        String status,
        String projectPath,
        int findingCount,
        List<SemgrepFinding> findings,
        List<String> errors,
        long durationMs
) {
}
