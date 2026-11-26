package com.teamvoid.aspm.api;

/** Normalized finding data returned by Semgrep. */
public record SemgrepFinding(
        String checkId,
        String path,
        Integer startLine,
        Integer startColumn,
        Integer endLine,
        Integer endColumn,
        String message,
        String severity,
        String confidence
) {
}
