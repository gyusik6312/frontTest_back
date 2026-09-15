package com.example.testfront.connection;

import com.example.testfront.user.User;
import com.example.testfront.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserConnectionApiTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private UserConnectionRepository connectionRepository;

    private User owner;
    private User target;

    @BeforeEach
    void setUp() {
        connectionRepository.deleteAll();
        userRepository.deleteAll();
        owner = userRepository.save(new User("요청자", "010-1111-1111"));
        target = userRepository.save(new User("연결 대상", "010-2222-2222"));
    }

    @Test
    void createsConnectionToUserFoundByPhoneNumber() throws Exception {
        mockMvc.perform(post("/api/users/{userId}/connections", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"010-2222-2222\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").value(owner.getId()))
                .andExpect(jsonPath("$.connectedUserId").value(target.getId()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
        var connections = connectionRepository.findAll();
        assertThat(connections).hasSize(1);
        assertThat(connections.getFirst().getUser().getId()).isEqualTo(owner.getId());
        assertThat(connections.getFirst().getConnectedUser().getId()).isEqualTo(target.getId());
        assertThat(connections.getFirst().getCreatedAt()).isNotNull();
        assertThat(connections.getFirst().getUpdatedAt()).isEqualTo(connections.getFirst().getCreatedAt());
    }

    @Test
    void rejectsUnknownPhoneNumber() throws Exception {
        assertRejected(owner.getId(), "010-9999-9999", 404,
                "해당 전화번호로 등록된 사용자가 없어 연결할 수 없습니다.");
        assertThat(connectionRepository.count()).isZero();
    }

    @Test
    void rejectsUnknownOwner() throws Exception {
        assertRejected(Long.MAX_VALUE, target.getPhoneNumber(), 404, "연결을 요청한 사용자가 존재하지 않습니다.");
        assertThat(connectionRepository.count()).isZero();
    }

    @Test
    void rejectsSelfConnection() throws Exception {
        assertRejected(owner.getId(), owner.getPhoneNumber(), 400, "자기 자신과는 연결할 수 없습니다.");
        assertThat(connectionRepository.count()).isZero();
    }

    @Test
    void rejectsDuplicateConnection() throws Exception {
        connectionRepository.saveAndFlush(new UserConnection(owner, target));
        assertRejected(owner.getId(), target.getPhoneNumber(), 409, "이미 연결된 사용자입니다.");
        assertThat(connectionRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsAmbiguousPhoneNumber() throws Exception {
        userRepository.saveAndFlush(new User("동일 번호 사용자", target.getPhoneNumber()));
        assertRejected(owner.getId(), target.getPhoneNumber(), 409,
                "해당 전화번호로 등록된 사용자가 여러 명이라 연결할 수 없습니다.");
        assertThat(connectionRepository.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"phoneNumber\":null}", "{\"phoneNumber\":\"\"}",
            "{\"phoneNumber\":\"   \"}", "{\"phoneNumber\":\"1111111111111111111111111111111\"}"})
    void rejectsInvalidPhoneNumber(String body) throws Exception {
        mockMvc.perform(post("/api/users/{userId}/connections", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertThat(connectionRepository.count()).isZero();
    }

    private void assertRejected(Long userId, String phoneNumber, int statusCode, String message) throws Exception {
        mockMvc.perform(post("/api/users/{userId}/connections", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"" + phoneNumber + "\"}"))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.message").value(message));
    }
}
