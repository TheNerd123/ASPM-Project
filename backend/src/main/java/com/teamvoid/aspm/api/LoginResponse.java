package com.teamvoid.aspm.api;

import com.teamvoid.aspm.domain.User;

import java.util.UUID;

/** Public login result. Password hashes are deliberately never included. */
public record LoginResponse(String token, UserSummary user) {

    public record UserSummary(UUID id, String name, String email, String role) {
        public static UserSummary from(User user) {
            return new UserSummary(user.getId(), user.getName(), user.getEmail(), user.getRole());
        }
    }
}
