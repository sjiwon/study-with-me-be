package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;

import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyWeekly -> StudyWeeklyApiController 테스트")
class StudyWeeklyApiControllerTest extends ControllerTest {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final MultipartFile files1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
    private final MultipartFile files2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
    private final MultipartFile files3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
    private final MultipartFile files4 = createMultipleMockMultipartFile("hello4.png", "image/png");

    @Nested
    @DisplayName("스터디 주차 생성 API [POST /api/studies/{studyId}/weeks] - AccessToken 필수")
    class CreateWeekly {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 주차를 생성할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(dateTimeFormatter))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(dateTimeFormatter))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Create/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files")
                                                    .description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title")
                                                    .description("스터디 주차 제목"),
                                            parameterWithName("content")
                                                    .description("스터디 주차 내용"),
                                            parameterWithName("startDate")
                                                    .description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate")
                                                    .description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists")
                                                    .description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance")
                                                    .description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차를 생성한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(createStudyWeeklyUseCase.invoke(any())).willReturn(1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(dateTimeFormatter))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(dateTimeFormatter))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.weeklyId").value(1L)
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Create/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files")
                                                    .description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title")
                                                    .description("스터디 주차 제목"),
                                            parameterWithName("content")
                                                    .description("스터디 주차 내용"),
                                            parameterWithName("startDate")
                                                    .description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate")
                                                    .description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance")
                                                    .description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    responseFields(
                                            fieldWithPath("weeklyId")
                                                    .description("생성한 Weekly ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차 수정 API [PATCH /api/studies/{studyId}/weeks/{weeklyId}] - AccessToken 필수")
    class UpdateWeekly {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{weeklyId}";
        private static final Long STUDY_ID = 1L;
        private static final Long WEEKLY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 주차를 수정할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(dateTimeFormatter))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(dateTimeFormatter))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Update/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files")
                                                    .description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title")
                                                    .description("스터디 주차 제목"),
                                            parameterWithName("content")
                                                    .description("스터디 주차 내용"),
                                            parameterWithName("startDate")
                                                    .description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate")
                                                    .description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists")
                                                    .description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance")
                                                    .description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("해당 주차 정보를 찾지 못하면 수정할 수 없다")
        void throwExceptionByWeeklyNotFound() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND))
                    .when(updateStudyWeeklyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(dateTimeFormatter))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(dateTimeFormatter))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.WEEKLY_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Update/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files")
                                                    .description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title")
                                                    .description("스터디 주차 제목"),
                                            parameterWithName("content")
                                                    .description("스터디 주차 내용"),
                                            parameterWithName("startDate")
                                                    .description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate")
                                                    .description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists")
                                                    .description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance")
                                                    .description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차를 수정한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doNothing()
                    .when(updateStudyWeeklyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(dateTimeFormatter))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(dateTimeFormatter))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Update/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files")
                                                    .description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title")
                                                    .description("스터디 주차 제목"),
                                            parameterWithName("content")
                                                    .description("스터디 주차 내용"),
                                            parameterWithName("startDate")
                                                    .description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate")
                                                    .description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists")
                                                    .description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance")
                                                    .description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차 삭제 API [DELETE /api/studies/{studyId}/weeks/{weeklyId}] - AccessToken 필수")
    class DeleteWeekly {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{weeklyId}";
        private static final Long STUDY_ID = 1L;
        private static final Long WEEKLY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 주차를 삭제할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Delete/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("삭제할 주차 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("가장 최신 주차가 아니면 해당 주차 정보를 삭제할 수 없다")
        void throwExceptionBySpecificWeekIsNotLatestWeek() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE))
                    .when(deleteStudyWeeklyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Delete/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("삭제할 주차 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차를 삭제한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Delete/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("삭제할 주차 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
