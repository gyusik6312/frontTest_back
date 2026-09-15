package com.example.testfront.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.List<User> findAllByPhoneNumber(String phoneNumber);
}
