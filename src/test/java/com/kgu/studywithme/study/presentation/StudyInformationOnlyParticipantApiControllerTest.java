package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.NoticeInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyMember;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.WeeklyInformation;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.PDF_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study -> StudyInformationOnlyParticipantApiController 테스트")
class StudyInformationOnlyParticipantApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 신청자 조회 API [GET /api/studies/{studyId}/applicants] - AccessToken 필수")
    class GetApplicants {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, PARTICIPANT_ID, false);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 신청자 정보를 조회할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Applicant/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 신청자 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(queryApplicantByIdUseCase.invoke(any()))
                    .willReturn(
                            List.of(
                                    new StudyApplicantInformation(
                                            1L,
                                            JIWON.getNickname().getValue(),
                                            85,
                                            LocalDateTime.now().minusDays(1)
                                    ),
                                    new StudyApplicantInformation(
                                            2L,
                                            GHOST.getNickname().getValue(),
                                            72,
                                            LocalDateTime.now().minusDays(3)
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Applicant/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id")
                                                    .description("신청자 ID(PK)"),
                                            fieldWithPath("result[].nickname")
                                                    .description("신청자 닉네임"),
                                            fieldWithPath("result[].score")
                                                    .description("신청자 점수"),
                                            fieldWithPath("result[].applyDate")
                                                    .description("신청 날짜")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 공지사항 조회 API [GET /api/studies/{studyId}/notices] - AccessToken 필수")
    class GetNotices {
        private static final String BASE_URL = "/api/studies/{studyId}/notices";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 스터디 공지사항 조회에 실패한다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Notice/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 공지사항을 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(queryNoticeByIdUseCase.invoke(any()))
                    .willReturn(
                            List.of(
                                    new NoticeInformation(
                                            2L,
                                            "스터디 공지사항 [중요]",
                                            "Hello World",
                                            LocalDateTime.now().minusDays(1),
                                            LocalDateTime.now().minusDays(1),
                                            new StudyMember(1L, JIWON.getNickname()),
                                            List.of(
                                                    new NoticeInformation.CommentInformation(
                                                            2L,
                                                            2L,
                                                            "OK~",
                                                            LocalDateTime.now().minusHours(3),
                                                            new StudyMember(1L, JIWON.getNickname())
                                                    ),
                                                    new NoticeInformation.CommentInformation(
                                                            1L,
                                                            2L,
                                                            "OK~",
                                                            LocalDateTime.now().minusHours(9),
                                                            new StudyMember(2L, GHOST.getNickname())
                                                    )
                                            )
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Notice/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id")
                                                    .description("공지사항 ID(PK)"),
                                            fieldWithPath("result[].title")
                                                    .description("공지사항 제목"),
                                            fieldWithPath("result[].content")
                                                    .description("공지사항 내용"),
                                            fieldWithPath("result[].createdAt")
                                                    .description("공지사항 작성 날짜"),
                                            fieldWithPath("result[].lastModifiedAt")
                                                    .description("공지사항 수정 날짜"),
                                            fieldWithPath("result[].writer.id")
                                                    .description("공지사항 작성자 ID(PK)"),
                                            fieldWithPath("result[].writer.nickname")
                                                    .description("공지사항 작성자 닉네임"),
                                            fieldWithPath("result[].comments[].id")
                                                    .description("공지사항 댓글 ID(PK)"),
                                            fieldWithPath("result[].comments[].noticeId")
                                                    .description("공지사항 ID(PK)"),
                                            fieldWithPath("result[].comments[].content")
                                                    .description("공지사항 댓글 내용"),
                                            fieldWithPath("result[].comments[].writtenDate")
                                                    .description("공지사항 댓글 작성/수정 날짜"),
                                            fieldWithPath("result[].comments[].writer.id")
                                                    .description("공지사항 댓글 작성자 ID(PK)"),
                                            fieldWithPath("result[].comments[].writer.nickname")
                                                    .description("공지사항 댓글 작성자 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 출석 정보 조회 API [GET /api/studies/{studyId}/attendances] - AccessToken 필수")
    class GetAttendances {
        private static final String BASE_URL = "/api/studies/{studyId}/attendances";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 스터디 출석 정보를 조회할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Attendance/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 출석 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(queryAttendanceByIdUseCase.invoke(any()))
                    .willReturn(
                            List.of(
                                    new AttendanceInformation(
                                            new StudyMember(1L, JIWON.getNickname()),
                                            List.of(
                                                    new AttendanceInformation.AttendanceSummary(1, ATTENDANCE.getValue()),
                                                    new AttendanceInformation.AttendanceSummary(2, ATTENDANCE.getValue()),
                                                    new AttendanceInformation.AttendanceSummary(3, NON_ATTENDANCE.getValue())
                                            )
                                    ),
                                    new AttendanceInformation(
                                            new StudyMember(2L, GHOST.getNickname()),
                                            List.of(
                                                    new AttendanceInformation.AttendanceSummary(1, ATTENDANCE.getValue()),
                                                    new AttendanceInformation.AttendanceSummary(2, LATE.getValue()),
                                                    new AttendanceInformation.AttendanceSummary(3, NON_ATTENDANCE.getValue())
                                            )
                                    ),
                                    new AttendanceInformation(
                                            new StudyMember(3L, ANONYMOUS.getNickname()),
                                            List.of(
                                                    new AttendanceInformation.AttendanceSummary(1, LATE.getValue()),
                                                    new AttendanceInformation.AttendanceSummary(2, ABSENCE.getValue())
                                            )
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Attendance/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].member.id")
                                                    .description("스터디 참여자 ID(PK)"),
                                            fieldWithPath("result[].member.nickname")
                                                    .description("스터디 참여자 닉네임"),
                                            fieldWithPath("result[].summaries[].week")
                                                    .description("스터디 주차"),
                                            fieldWithPath("result[].summaries[].attendanceStatus")
                                                    .description("해당 주차 출석 상태")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 정보 조회 API [GET /api/studies/{studyId}/weeks] - AccessToken 필수")
    class GetWeeks {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 스터디 주차별 정보를 조회할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Weekly/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차별 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(queryWeeklyByIdUseCase.invoke(any()))
                    .willReturn(
                            List.of(
                                    new WeeklyInformation(
                                            1L,
                                            STUDY_WEEKLY_1.getTitle(),
                                            STUDY_WEEKLY_1.getContent(),
                                            1,
                                            STUDY_WEEKLY_1.getPeriod().toPeriod(),
                                            STUDY_WEEKLY_1.isAssignmentExists(),
                                            STUDY_WEEKLY_1.isAutoAttendance(),
                                            new StudyMember(1L, JIWON.getNickname()),
                                            List.of(
                                                    new WeeklyInformation.WeeklyAttachment(
                                                            1L,
                                                            PDF_FILE.getUploadFileName(),
                                                            PDF_FILE.getLink()
                                                    )
                                            ),
                                            List.of(
                                                    new WeeklyInformation.WeeklySubmit(
                                                            1L,
                                                            JIWON.getNickname(),
                                                            1L,
                                                            UploadAssignment.withLink("https://notion.so/jiwon")
                                                    ),
                                                    new WeeklyInformation.WeeklySubmit(
                                                            2L,
                                                            GHOST.getNickname(),
                                                            1L,
                                                            UploadAssignment.withLink("https://notion.so/ghost")
                                                    )
                                            )
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Private/Weekly/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id")
                                                    .description("스터디 주차 ID(PK)"),
                                            fieldWithPath("result[].title")
                                                    .description("스터디 주차 제목"),
                                            fieldWithPath("result[].content")
                                                    .description("스터디 주차 내용"),
                                            fieldWithPath("result[].week")
                                                    .description("스터디 주차 Week 정보"),
                                            fieldWithPath("result[].period.startDate")
                                                    .description("스터디 주차 시작날짜"),
                                            fieldWithPath("result[].period.endDate")
                                                    .description("스터디 주차 종료날짜"),
                                            fieldWithPath("result[].assignmentExists")
                                                    .description("스터디 주차 과제 존재 여부"),
                                            fieldWithPath("result[].autoAttendance")
                                                    .description("스터디 주차 자동 출석 여부"),
                                            fieldWithPath("result[].creator.id")
                                                    .description("스터디 주차 생성자 ID(PK)"),
                                            fieldWithPath("result[].creator.nickname")
                                                    .description("스터디 주차 생성자 닉네임"),
                                            fieldWithPath("result[].attachments[]")
                                                    .description("스터디 주차 첨부파일")
                                                    .optional(),
                                            fieldWithPath("result[].attachments[].weeklyId")
                                                    .description("스터디 주차 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].attachments[].uploadFileName")
                                                    .description("스터디 주차 첨부파일 업로드 파일명")
                                                    .optional(),
                                            fieldWithPath("result[].attachments[].link")
                                                    .description("스터디 주차 첨부파일 S3 업로드 링크")
                                                    .optional(),
                                            fieldWithPath("result[].submits[]")
                                                    .description("스터디 주차 제출 과제")
                                                    .optional(),
                                            fieldWithPath("result[].submits[].participant.id")
                                                    .description("스터디 주차 과제 제출자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].submits[].participant.nickname")
                                                    .description("스터디 주차 과제 제출자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result[].submits[].weeklyId")
                                                    .description("스터디 주차 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].submits[].submitType")
                                                    .description("스터디 주차 과제 제출 타입")
                                                    .optional(),
                                            fieldWithPath("result[].submits[].submitFileName")
                                                    .description("스터디 주차 과제 제출 파일명")
                                                    .optional()
                                                    .attributes(constraint("링크 제출 = null / 파일 제출 = 원본 파일명")),
                                            fieldWithPath("result[].submits[].submitLink")
                                                    .description("스터디 주차 과제 제출 링크")
                                                    .optional()
                                    )
                            )
                    );
        }
    }
}
