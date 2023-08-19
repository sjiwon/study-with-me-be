package com.kgu.studywithme.auth.infrastructure.oauth.naver.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kgu.studywithme.auth.domain.oauth.OAuthUserResponse;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverUserResponse(
        String resultcode,
        String message,
        Response response
) implements OAuthUserResponse {
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Response(
            String id,
            String name,
            String nickname,
            String email,
            String profileImage,
            String age,
            String gender,
            String birthDay,
            String birthYear,
            String mobile
    ) {
    }

    @Override
    public String name() {
        return response.name;
    }

    @Override
    public String email() {
        return response.email;
    }
}
