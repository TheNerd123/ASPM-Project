package com.teamvoid.aspm.api;

import jakarta.validation.constraints.NotBlank;

public record CreateApplicationRequest(
        @NotBlank(message = "name is required") String name,
        String repositoryUrl
) {
}
