package com.kgu.studywithme.acceptance.upload;

import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.multipartRequest;

public class UploadAcceptanceFixture {
    public static ValidatableResponse 스터디_주차_글_내부_이미지를_업로드한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/image")
                .build()
                .toUri()
                .getPath();

        final Map<String, String> params = new HashMap<>();
        params.put("type", "weekly");

        return multipartRequest("hello4.png", params, accessToken, uri);
    }

    public static ValidatableResponse 스터디_설명_내부_이미지를_업로드한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/image")
                .build()
                .toUri()
                .getPath();

        final Map<String, String> params = new HashMap<>();
        params.put("type", "description");

        return multipartRequest("hello4.png", params, accessToken, uri);
    }
}
