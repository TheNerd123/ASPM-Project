package com.teamvoid.aspm.service;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, UUID id) {
        super("No " + resource + " was found for id " + id);
    }
}
