package com.example.testfront.connection;

import com.example.testfront.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_connections", uniqueConstraints = @UniqueConstraint(
        name = "uk_user_connection_pair", columnNames = {"user_id", "connected_user_id"}))
public class UserConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connected_user_id", nullable = false)
    private User connectedUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserConnection() {
    }

    public UserConnection(User user, User connectedUser) {
        this.user = user;
        this.connectedUser = connectedUser;
    }

    @PrePersist
    private void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public User getConnectedUser() { return connectedUser; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
