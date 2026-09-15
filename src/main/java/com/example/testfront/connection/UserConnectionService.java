package com.example.testfront.connection;

import com.example.testfront.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.http.HttpStatus.*;

@Service
public class UserConnectionService {
    private final UserRepository userRepository;
    private final UserConnectionRepository connectionRepository;

    public UserConnectionService(UserRepository userRepository, UserConnectionRepository connectionRepository) {
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository;
    }

    @Transactional
    public ConnectionResponse create(Long userId, CreateConnectionRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ConnectionException(NOT_FOUND, "연결을 요청한 사용자가 존재하지 않습니다."));
        var matches = userRepository.findAllByPhoneNumber(request.phoneNumber());
        if (matches.isEmpty()) {
            throw new ConnectionException(NOT_FOUND, "해당 전화번호로 등록된 사용자가 없어 연결할 수 없습니다.");
        }
        if (matches.size() > 1) {
            throw new ConnectionException(CONFLICT, "해당 전화번호로 등록된 사용자가 여러 명이라 연결할 수 없습니다.");
        }
        var connectedUser = matches.getFirst();
        if (userId.equals(connectedUser.getId())) {
            throw new ConnectionException(BAD_REQUEST, "자기 자신과는 연결할 수 없습니다.");
        }
        if (connectionRepository.existsByUser_IdAndConnectedUser_Id(userId, connectedUser.getId())) {
            throw new ConnectionException(CONFLICT, "이미 연결된 사용자입니다.");
        }
        return ConnectionResponse.from(connectionRepository.saveAndFlush(new UserConnection(user, connectedUser)));
    }
}
