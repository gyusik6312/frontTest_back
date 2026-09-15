package com.example.testfront.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {
    @EntityGraph(attributePaths = {"user", "connectedUser"})
    List<UserConnection> findAllByUser_IdOrderByIdDesc(Long userId);

    Optional<UserConnection> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndConnectedUser_Id(Long userId, Long connectedUserId);
}
