package com.teamvoid.aspm.domain;

import java.time.Instant;
import java.util.UUID;

public record Finding(UUID id, UUID applicationId, Severity severity, String title, FindingStatus status, Instant createdAt) {
}
