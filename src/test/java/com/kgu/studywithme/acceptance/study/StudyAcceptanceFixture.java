package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.fixture.StudyFixture;
import com.kgu.studywithme.study.presentation.dto.request.CreateStudyRequest;
import com.kgu.studywithme.study.presentation.dto.request.UpdateStudyRequest;
import com.kgu.studywithme.studyparticipant.presentation.dto.request.RejectParticipationRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.*;

public class StudyAcceptanceFixture {
    public static ValidatableResponse 스터디를_생성한다(
            final String accessToken,
            final StudyFixture fixture
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/study")
                .build()
                .toUri()
                .getPath();

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

        return postRequest(accessToken, request, uri);
    }

    public static Long 스터디를_생성하고_PK를_얻는다(
            final String accessToken,
            final StudyFixture fixture
    ) {
        return 스터디를_생성한다(accessToken, fixture)
                .extract()
                .jsonPath()
                .getLong("studyId");
    }

    public static ValidatableResponse 스터디를_수정한다(
            final String accessToken,
            final Long studyId,
            final StudyFixture fixture
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}")
                .build(studyId)
                .getPath();

        final UpdateStudyRequest request = new UpdateStudyRequest(
                fixture.getName().getValue(),
                fixture.getDescription().getValue(),
                fixture.getCapacity().getValue(),
                fixture.getCategory().getId(),
                fixture.getThumbnail().getImageName(),
                fixture.getType().getValue(),
                (fixture.getLocation() == null) ? null : fixture.getLocation().getProvince(),
                (fixture.getLocation() == null) ? null : fixture.getLocation().getCity(),
                true,
                fixture.getMinimumAttendanceForGraduation(),
                fixture.getHashtags()
        );

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 스터디를_종료시킨다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}")
                .build(studyId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_참여_신청을_한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/applicants")
                .build(studyId)
                .getPath();

        return postRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_참여_신청을_취소한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/applicants")
                .build(studyId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_신청자에_대한_참여를_승인한다(
            final String accessToken,
            final Long studyId,
            final Long applierId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/applicants/{applierId}/approve")
                .build(studyId, applierId)
                .getPath();

        return patchRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_신청자에_대한_참여를_거절한다(
            final String accessToken,
            final Long studyId,
            final Long applierId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/applicants/{applierId}/reject")
                .build(studyId, applierId)
                .getPath();

        final RejectParticipationRequest request = new RejectParticipationRequest("Sorry...");

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 스터디_팀장_권한을_위임한다(
            final String accessToken,
            final Long studyId,
            final Long participantId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/participants/{participantId}/delegation")
                .build(studyId, participantId)
                .getPath();

        return patchRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_참여를_취소한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/participants/leave")
                .build(studyId)
                .getPath();

        return patchRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디를_졸업한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/graduate")
                .build(studyId)
                .getPath();

        return patchRequest(accessToken, uri);
    }
}
