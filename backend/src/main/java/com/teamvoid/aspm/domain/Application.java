package com.teamvoid.aspm.domain;

import java.time.Instant;
import java.util.UUID;

public record Application(UUID id, String name, String repositoryUrl, AssetStatus status, Instant createdAt) {
}
