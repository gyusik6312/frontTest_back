package com.example.testfront.connection;

import java.time.Instant;

public record ConnectionResponse(Long id, Long userId, Long connectedUserId,
                                 Instant createdAt, Instant updatedAt) {
    public static ConnectionResponse from(UserConnection connection) {
        return new ConnectionResponse(connection.getId(), connection.getUser().getId(),
                connection.getConnectedUser().getId(), connection.getCreatedAt(), connection.getUpdatedAt());
    }
}
