package com.kgu.studywithme.acceptance.category;

import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.getRequest;

public class CategoryAcceptanceFixture {
    public static ValidatableResponse 모든_스터디_카테고리를_조회한다() {
        final String uri = UriComponentsBuilder
                .fromPath("/api/categories")
                .build()
                .toUri()
                .getPath();

        return getRequest(uri);
    }
}
