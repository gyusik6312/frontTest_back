package com.example.testfront.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse findByPhoneNumber(String phoneNumber) {
        var users = userRepository.findAllByPhoneNumber(phoneNumber);
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "등록되지 않은 전화번호입니다.");
        }
        if (users.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 전화번호로 등록된 사용자가 여러 명입니다.");
        }
        return UserResponse.from(users.getFirst());
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        User user = userRepository.save(new User(request.name(), request.phoneNumber()));
        return UserResponse.from(user);
    }
}
