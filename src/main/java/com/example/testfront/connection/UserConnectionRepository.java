package com.example.testfront.connection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {
    boolean existsByUser_IdAndConnectedUser_Id(Long userId, Long connectedUserId);
}
