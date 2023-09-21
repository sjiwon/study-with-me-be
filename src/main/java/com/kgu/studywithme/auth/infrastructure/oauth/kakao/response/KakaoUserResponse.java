package com.kgu.studywithme.auth.infrastructure.oauth.kakao.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthUserResponse;

import java.time.LocalDateTime;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserResponse(
        long id,
        LocalDateTime connectedAt,
        KakaoAccount kakaoAccount
) implements OAuthUserResponse {
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
            Profile profile,
            boolean profileNicknameNeedsAgreement,
            boolean profileImageNeesAgreement,
            String name,
            boolean nameNeedsAgreement,
            String email,
            boolean emailNeedsAgreement,
            boolean isEmailValid,
            boolean isEmailVerified,
            String ageRange,
            boolean ageRangeNeedsAgreement,
            String birthYear,
            boolean birthYearNeedsAgreement,
            String birthDay,
            String birthDayType,
            boolean birthDayNeedsAgreement,
            String gender,
            boolean genderNeedsAgreement,
            String phoneNumber,
            boolean phoneNumberNeedsAgreement
    ) {
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Profile(
            String nickname,
            String thumbnailImageUrl,
            String profileImageUrl,
            boolean isDefaultImage
    ) {
    }

    @Override
    public String name() {
        return kakaoAccount.profile.nickname;
    }

    @Override
    public String email() {
        return kakaoAccount.email;
    }
}
