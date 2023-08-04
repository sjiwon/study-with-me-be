package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.fixture.StudyFixture;
import com.kgu.studywithme.common.fixture.StudyWeeklyFixture;
import com.kgu.studywithme.study.presentation.dto.request.CreateStudyRequest;
import com.kgu.studywithme.study.presentation.dto.request.UpdateStudyRequest;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.presentation.dto.request.ManualAttendanceRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.UpdateStudyNoticeCommentRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.UpdateStudyNoticeRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.WriteStudyNoticeCommentRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.WriteStudyNoticeRequest;
import com.kgu.studywithme.studyparticipant.presentation.dto.request.RejectParticipationRequest;
import com.kgu.studywithme.studyreview.presentation.dto.request.UpdateStudyReviewRequest;
import com.kgu.studywithme.studyreview.presentation.dto.request.WriteStudyReviewRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.*;

public class StudyAcceptanceFixture {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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

    public static ValidatableResponse 스터디_리뷰를_작성한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/review")
                .build(studyId)
                .getPath();

        final WriteStudyReviewRequest request = new WriteStudyReviewRequest("Good!");

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 작성한_스터디_리뷰를_수정한다(
            final String accessToken,
            final Long studyId,
            final Long reviewId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/reviews/{reviewId}")
                .build(studyId, reviewId)
                .getPath();

        final UpdateStudyReviewRequest request = new UpdateStudyReviewRequest("Bad..");

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 작성한_스터디_리뷰를_삭제한다(
            final String accessToken,
            final Long studyId,
            final Long reviewId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/reviews/{reviewId}")
                .build(studyId, reviewId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_주차를_생성한다(
            final String accessToken,
            final Long studyId,
            final StudyWeeklyFixture fixture
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/week")
                .build(studyId)
                .getPath();

        final Map<String, String> params = new HashMap<>();
        params.put("title", fixture.getTitle());
        params.put("content", fixture.getContent());
        params.put("startDate", fixture.getPeriod().getStartDate().format(DATE_TIME_FORMATTER));
        params.put("endDate", fixture.getPeriod().getEndDate().format(DATE_TIME_FORMATTER));
        params.put("assignmentExists", String.valueOf(fixture.isAssignmentExists()));
        params.put("autoAttendance", String.valueOf(fixture.isAutoAttendance()));

        return multipartRequest(
                List.of("hello2.hwpx", "hello4.png"),
                params,
                accessToken,
                uri
        );
    }

    public static ValidatableResponse 스터디_주차를_수정한다(
            final String accessToken,
            final Long studyId,
            final Long weeklyId,
            final StudyWeeklyFixture fixture
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/weeks/{weeklyId}")
                .build(studyId, weeklyId)
                .getPath();

        final Map<String, String> params = new HashMap<>();
        params.put("title", fixture.getTitle());
        params.put("content", fixture.getContent());
        params.put("startDate", fixture.getPeriod().getStartDate().format(DATE_TIME_FORMATTER));
        params.put("endDate", fixture.getPeriod().getEndDate().format(DATE_TIME_FORMATTER));
        params.put("assignmentExists", String.valueOf(fixture.isAssignmentExists()));
        params.put("autoAttendance", String.valueOf(fixture.isAutoAttendance()));

        return multipartRequest(
                List.of("hello1.txt", "hello3.pdf"),
                params,
                accessToken,
                uri
        );
    }

    public static ValidatableResponse 스터디_주차를_삭제한다(
            final String accessToken,
            final Long studyId,
            final Long weeklyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/weeks/{weeklyId}")
                .build(studyId, weeklyId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }

    public static ValidatableResponse 해당_주차에_과제를_제출한다(
            final String accessToken,
            final Long studyId,
            final Long weeklyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/weeks/{weeklyId}/assignment")
                .build(studyId, weeklyId)
                .getPath();

        final Map<String, String> params = new HashMap<>();
        params.put("type", "link");
        params.put("link", "https://notion.so");

        return multipartRequest(
                params,
                accessToken,
                uri
        );
    }

    public static ValidatableResponse 해당_주차에_제출한_과제를_수정한다(
            final String accessToken,
            final Long studyId,
            final Long weeklyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/weeks/{weeklyId}/assignment/edit")
                .build(studyId, weeklyId)
                .getPath();

        final Map<String, String> params = new HashMap<>();
        params.put("type", "file");

        return multipartRequest(
                "hello3.pdf",
                params,
                accessToken,
                uri
        );
    }

    public static ValidatableResponse 사용자에_대한_해당_주차_출석_정보를_수정한다(
            final String accessToken,
            final Long studyId,
            final Long memberId,
            final int week,
            final AttendanceStatus status
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/attendance/{memberId}")
                .build(studyId, memberId)
                .getPath();

        final ManualAttendanceRequest request = new ManualAttendanceRequest(week, status.getDescription());

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 스터디_공지사항을_작성한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/notice")
                .build(studyId)
                .getPath();

        final WriteStudyNoticeRequest request = new WriteStudyNoticeRequest("hello", "content");

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 작성한_스터디_공지사항을_수정한다(
            final String accessToken,
            final Long studyId,
            final Long noticeId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/notices/{noticeId}")
                .build(studyId, noticeId)
                .getPath();

        final UpdateStudyNoticeRequest request = new UpdateStudyNoticeRequest("hello", "content");

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 작성한_스터디_공지사항을_삭제한다(
            final String accessToken,
            final Long studyId,
            final Long noticeId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/notices/{noticeId}")
                .build(studyId, noticeId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_공지사항에_댓글을_작성한다(
            final String accessToken,
            final Long noticeId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/notices/{noticeId}/comment")
                .build(noticeId)
                .getPath();

        final WriteStudyNoticeCommentRequest request = new WriteStudyNoticeCommentRequest("ok");

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 스터디_공지사항에_작성한_댓글을_수정한다(
            final String accessToken,
            final Long noticeId,
            final Long commentId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/notices/{noticeId}/comments/{commentId}")
                .build(noticeId, commentId)
                .getPath();

        final UpdateStudyNoticeCommentRequest request = new UpdateStudyNoticeCommentRequest("ok");

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 스터디_공지사항에_작성한_댓글을_삭제한다(
            final String accessToken,
            final Long noticeId,
            final Long commentId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/notices/{noticeId}/comments/{commentId}")
                .build(noticeId, commentId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_기본_정보를_조회한다(final Long studyId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}")
                .build(studyId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 스터디_리뷰를_조회한다(final Long studyId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/reviews")
                .build(studyId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 스터디_참여자를_조회한다(final Long studyId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/participants")
                .build(studyId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 스터디_신청자를_조회한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/applicants")
                .build(studyId)
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_공지사항을_조회한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/notices")
                .build(studyId)
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_출석_정보를_조회한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/attendances")
                .build(studyId)
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 스터디_주차별_정보를_조회한다(
            final String accessToken,
            final Long studyId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/studies/{studyId}/weeks")
                .build(studyId)
                .getPath();

        return getRequest(accessToken, uri);
    }
}
