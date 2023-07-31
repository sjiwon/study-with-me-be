package com.kgu.studywithme.acceptance.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

public class CommonResponseFixture {
    public static void 상태코드_200을_응답한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    public static void 상태코드_201을_응답한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
    }

    public static void 상태코드_204를_응답한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    public static void 상태코드_404를_응답한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }
}
