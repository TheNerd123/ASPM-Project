package com.teamvoid.aspm.api;

public record DashboardSummary(
        long totalApplications,
        long activeApplications,
        long inactiveApplications,
        long openFindings,
        long criticalFindings,
        long highFindings,
        long mediumFindings,
        long lowFindings
) {
}
