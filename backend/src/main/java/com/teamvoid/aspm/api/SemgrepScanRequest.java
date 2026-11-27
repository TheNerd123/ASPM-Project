package com.teamvoid.aspm.api;

import jakarta.validation.constraints.NotBlank;

/** Request to run a Semgrep scan against a local project directory. */
public record SemgrepScanRequest(
        @NotBlank(message = "projectPath is required") String projectPath,
        String config
) {
}
