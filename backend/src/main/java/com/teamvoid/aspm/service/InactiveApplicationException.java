package com.teamvoid.aspm.service;

import java.util.UUID;

public class InactiveApplicationException extends RuntimeException {
    public InactiveApplicationException(UUID applicationId) {
        super("Application " + applicationId + " is inactive and cannot be scanned");
    }
}
