package com.example.testfront.connection;

import java.time.Instant;

public record ConnectedUserResponse(Long id, Long userId, Long connectedUserId,
                                    String name, String phoneNumber,
                                    Instant createdAt, Instant updatedAt) {
    public static ConnectedUserResponse from(UserConnection connection) {
        var target = connection.getConnectedUser();
        return new ConnectedUserResponse(connection.getId(), connection.getUser().getId(),
                target.getId(), target.getName(), target.getPhoneNumber(),
                connection.getCreatedAt(), connection.getUpdatedAt());
    }
}
