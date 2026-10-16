package com.teamvoid.aspm.api;

public record DashboardSummary(
        long totalApplications,
        long activeApplications,
        long archivedApplications,
        long openFindings,
        long criticalFindings,
        long highFindings,
        long mediumFindings,
        long lowFindings
) {
}
