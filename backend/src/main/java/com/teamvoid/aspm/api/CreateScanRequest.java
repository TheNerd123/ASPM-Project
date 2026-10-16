package com.teamvoid.aspm.api;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateScanRequest(
        @NotNull(message = "applicationId is required") UUID applicationId,
        @NotNull(message = "scannerId is required") UUID scannerId
) {
}
