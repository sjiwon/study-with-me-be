package com.kgu.studywithme.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public record RegistrationMemberRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotNull(message = "생년월일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,

        @NotBlank(message = "전화번호는 필수입니다.")
        String phone,

        @NotBlank(message = "성별은 필수입니다.")
        String gender,

        @NotBlank(message = "거주지는 필수입니다.")
        String province,

        @NotBlank(message = "거주지는 필수입니다.")
        String city,

        @NotNull(message = "이메일 수신 동의 여부는 필수입니다.")
        Boolean emailOptIn,

        @NotEmpty(message = "관심사는 하나 이상 등록해야 합니다.")
        Set<Long> interests
) {
}
