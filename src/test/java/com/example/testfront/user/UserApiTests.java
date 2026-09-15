package com.example.testfront.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void createsUserWithGeneratedIdAndTimestamps() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"홍길동\",\"phoneNumber\":\"010-1234-5678\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        var users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getName()).isEqualTo("홍길동");
        assertThat(users.getFirst().getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(users.getFirst().getCreatedAt()).isNotNull();
        assertThat(users.getFirst().getUpdatedAt()).isEqualTo(users.getFirst().getCreatedAt());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null}", "{\"name\":\"\"}", "{\"name\":\"   \"}"})
    void rejectsMissingOrBlankName(String body) throws Exception {
        String fields = body.substring(1, body.length() - 1);
        assertInvalidRequest("{\"phoneNumber\":\"010-1234-5678\"" + (fields.isEmpty() ? "" : "," + fields) + "}");
    }

    @Test
    void rejectsNameLongerThan100Characters() throws Exception {
        assertInvalidRequest("{\"name\":\"" + "a".repeat(101) + "\",\"phoneNumber\":\"010-1234-5678\"}");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ",\"phoneNumber\":null", ",\"phoneNumber\":\"\"", ",\"phoneNumber\":\"   \""})
    void rejectsMissingOrBlankPhoneNumber(String phoneField) throws Exception {
        assertInvalidRequest("{\"name\":\"홍길동\"" + phoneField + "}");
    }

    @Test
    void rejectsPhoneNumberLongerThan30Characters() throws Exception {
        assertInvalidRequest("{\"name\":\"홍길동\",\"phoneNumber\":\"" + "1".repeat(31) + "\"}");
    }

    private void assertInvalidRequest(String body) throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.count()).isZero();
    }
}
