package com.teamvoid.aspm.domain;

import java.time.Instant;
import java.util.UUID;

public record Scan(UUID id, UUID applicationId, UUID scannerId, ScanStatus status, int findingsCount, Instant startedAt) {
}
