package com.teamvoid.aspm.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UpdateApplicationRequest(
	@NotBlank(message = "name is required")
	@Size(max = 255, message = "name must not exceed 255 characters")
	String name,

	@URL(message = "repositoryURL must be a valid URL")
	@Size(max = 255, message = "repositoryURL must not exceed 255 characters")
	String repositoryUrl
) {
}
