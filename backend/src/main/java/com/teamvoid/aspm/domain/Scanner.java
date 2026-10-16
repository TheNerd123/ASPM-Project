package com.teamvoid.aspm.domain;

import java.util.UUID;

public record Scanner(UUID id, String name, String type, boolean enabled) {
}
