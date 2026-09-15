package com.example.testfront.user;

import java.time.Instant;

public record UserResponse(Long id, String name, String phoneNumber, Instant createdAt, Instant updatedAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getPhoneNumber(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
