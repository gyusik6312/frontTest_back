package com.example.testfront.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserLookupApiTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @Test
    void returnsExistingUserWithoutCreatingAnotherUser() throws Exception {
        var user = userRepository.saveAndFlush(new User("로그인 사용자", "010-9876-5432"));
        long count = userRepository.count();

        mockMvc.perform(get("/api/users").param("phoneNumber", user.getPhoneNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("로그인 사용자"))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
        assertThat(userRepository.count()).isEqualTo(count);
    }

    @Test
    void rejectsUnknownPhoneNumber() throws Exception {
        long count = userRepository.count();
        mockMvc.perform(get("/api/users").param("phoneNumber", "010-0000-9876"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록되지 않은 전화번호입니다."));
        assertThat(userRepository.count()).isEqualTo(count);
    }

    @Test
    void rejectsAmbiguousPhoneNumber() throws Exception {
        userRepository.save(new User("사용자1", "010-5555-9876"));
        userRepository.saveAndFlush(new User("사용자2", "010-5555-9876"));
        mockMvc.perform(get("/api/users").param("phoneNumber", "010-5555-9876"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("해당 전화번호로 등록된 사용자가 여러 명입니다."));
    }

    @Test
    void rejectsMissingPhoneNumber() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "1111111111111111111111111111111"})
    void rejectsInvalidPhoneNumber(String phoneNumber) throws Exception {
        mockMvc.perform(get("/api/users").param("phoneNumber", phoneNumber))
                .andExpect(status().isBadRequest());
    }
}
