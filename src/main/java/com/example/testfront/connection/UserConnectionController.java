package com.example.testfront.connection;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/connections")
public class UserConnectionController {
    private final UserConnectionService connectionService;

    public UserConnectionController(UserConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConnectionResponse create(@PathVariable Long userId, @Valid @RequestBody CreateConnectionRequest request) {
        return connectionService.create(userId, request);
    }

    @GetMapping
    public java.util.List<ConnectedUserResponse> findAll(@PathVariable Long userId) {
        return connectionService.findAll(userId);
    }

    @DeleteMapping("/{connectionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long connectionId) {
        connectionService.delete(userId, connectionId);
    }
}
