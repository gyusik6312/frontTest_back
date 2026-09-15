package com.example.testfront.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FindUserRequest(
        @NotBlank(message = "전화번호는 필수입니다.")
        @Size(max = 30, message = "전화번호는 30자 이하여야 합니다.")
        String phoneNumber
) {
}
