package com.kgu.studywithme.acceptance.favorite;

import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.deleteRequest;
import static com.kgu.studywithme.acceptance.CommonRequestFixture.postRequest;

public class FavoriteAcceptanceFixture {
    public static ValidatableResponse 스터디를_찜_등록한다(final String accessToken, final Long studyId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/like")
                .build(studyId)
                .getPath();

        return postRequest(accessToken, uri);
    }

    public static ValidatableResponse 찜_등록한_스터디를_취소한다(final String accessToken, final Long studyId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/like")
                .build(studyId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }
}
