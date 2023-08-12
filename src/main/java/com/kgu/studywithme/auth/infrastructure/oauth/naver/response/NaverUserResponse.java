package com.kgu.studywithme.auth.infrastructure.oauth.naver.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;

public record NaverUserResponse(
        // required
        String name,
        String email,

        // other
        String resultCode,
        String message,
        String id,
        String nickname,
        String profileImage,
        String age,
        String gender,
        String birthYear,
        String birthDay,
        String mobile
) implements OAuthUserResponse {
    public record Response(
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("id") String id,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("profile_image") String profileImage,
            @JsonProperty("age") String age,
            @JsonProperty("gender") String gender,
            @JsonProperty("birthyear") String birthYear,
            @JsonProperty("birthday") String birthDay,
            @JsonProperty("mobile") String mobile
    ) {
    }

    public NaverUserResponse(
            @JsonProperty("resultcode") final String resultCode,
            @JsonProperty("message") final String message,
            @JsonProperty("response") final Response response
    ) {
        this(
                response.name,
                response.email,
                resultCode,
                message,
                response.id,
                response.nickname,
                response.profileImage,
                response.age,
                response.gender,
                response.birthYear,
                response.birthDay,
                response.mobile
        );
    }
}
