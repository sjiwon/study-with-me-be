package com.kgu.studywithme.acceptance.fixture;

import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.kgu.studywithme.acceptance.fixture.CommonRequestFixture.getRequest;

public class CategoryAcceptanceFixture {
    public static ValidatableResponse 모든_스터디_카테고리를_조회한다() {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/categories")
                .build()
                .toUri();

        return getRequest(uri.getPath());
    }
}
