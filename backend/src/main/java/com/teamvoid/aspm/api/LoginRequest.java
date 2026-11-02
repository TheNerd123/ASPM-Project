package com.teamvoid.aspm.api;

import jakarta.validation.constraints.NotBlank;

/** Credentials submitted by the login page. */
public class LoginRequest {

    private String email;
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String identifier() {
        String value = email != null && !email.isBlank() ? email : username;
        return value == null ? "" : value.trim();
    }
}
