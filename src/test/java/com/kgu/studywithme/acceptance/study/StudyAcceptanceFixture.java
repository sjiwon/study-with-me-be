package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.fixture.StudyFixture;
import com.kgu.studywithme.study.presentation.dto.request.CreateStudyRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.postRequest;

public class StudyAcceptanceFixture {
    public static ValidatableResponse 스터디를_생성한다(final String accessToken, final StudyFixture fixture) {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/study")
                .build()
                .toUri();
        final CreateStudyRequest request = new CreateStudyRequest(
                fixture.getName().getValue(),
                fixture.getDescription().getValue(),
                fixture.getCapacity().getValue(),
                fixture.getCategory().getId(),
                fixture.getThumbnail().getImageName(),
                fixture.getType().getValue(),
                (fixture.getLocation() == null) ? null : fixture.getLocation().getProvince(),
                (fixture.getLocation() == null) ? null : fixture.getLocation().getCity(),
                fixture.getMinimumAttendanceForGraduation(),
                fixture.getHashtags()
        );

        return postRequest(accessToken, request, uri.getPath());
    }
}
