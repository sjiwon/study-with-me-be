package com.kgu.studywithme.auth.infrastructure.oauth.kakao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;

public record KakaoUserResponse(
        // required
        String name,
        String email,

        // other
        String id,
        String profileImage,
        String gender,
        String ageRange
) implements OAuthUserResponse {
    public record KakaoAccount(
            @JsonProperty("profile") Profile profile,
            @JsonProperty("is_email_valid") boolean isEmailValid,
            @JsonProperty("is_email_verified") boolean isEmailVerified,
            @JsonProperty("email") String email,
            @JsonProperty("gender") String gender,
            @JsonProperty("age_range") String ageRange
    ) {
        public record Profile(
                @JsonProperty("nickname") String nickname,
                @JsonProperty("thumbnail_image_url") String thumbnailImage,
                @JsonProperty("profile_image_url") String profileImage,
                @JsonProperty("is_default_image") boolean isDefaultImage
        ) {
        }
    }

    public KakaoUserResponse(
            @JsonProperty("id") final String id,
            @JsonProperty("kakao_account") final KakaoAccount kakaoAccount
    ) {
        this(
                kakaoAccount.profile.nickname,
                kakaoAccount.email,
                id,
                kakaoAccount.profile.profileImage,
                kakaoAccount.gender,
                kakaoAccount.ageRange
        );
    }
}
