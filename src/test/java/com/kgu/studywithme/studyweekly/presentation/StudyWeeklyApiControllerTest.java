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

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyWeekly -> StudyWeeklyApiController 테스트")
class StudyWeeklyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 주차 생성 API [POST /api/studies/{studyId}/week] - AccessToken 필수")
    class createWeekly {
        private static final String BASE_URL = "/api/studies/{studyId}/week";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        private MultipartFile files1;
        private MultipartFile files2;
        private MultipartFile files3;
        private MultipartFile files4;

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);

            files1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
            files2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
            files3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
            files4 = createMultipleMockMultipartFile("hello4.png", "image/png");
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
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(DATE_TIME_FORMATTER))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(DATE_TIME_FORMATTER))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Create/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 주차를 생성한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(DATE_TIME_FORMATTER))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(DATE_TIME_FORMATTER))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Create/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차 수정 API [PATCH /api/studies/{studyId}/weeks/{week}] - AccessToken 필수")
    class updateWeekly {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{week}";
        private static final Integer WEEK = 1;
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        private MultipartFile files1;
        private MultipartFile files2;
        private MultipartFile files3;
        private MultipartFile files4;

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, ANONYMOUS_ID, false);

            files1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
            files2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
            files3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
            files4 = createMultipleMockMultipartFile("hello4.png", "image/png");
        }

        @Test
        @DisplayName("팀장이 아니라면 스터디 주차를 수정할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(DATE_TIME_FORMATTER))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(DATE_TIME_FORMATTER))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Update/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("해당 주차 정보를 찾지 못하면 수정할 수 없다")
        void throwExceptionByWeeklyNotFound() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND))
                    .when(updateStudyWeeklyUseCase)
                    .updateStudyWeekly(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(DATE_TIME_FORMATTER))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(DATE_TIME_FORMATTER))
                    .queryParam("assignmentExists", String.valueOf(STUDY_WEEKLY_1.isAssignmentExists()))
                    .queryParam("autoAttendance", String.valueOf(STUDY_WEEKLY_1.isAutoAttendance()));

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.WEEKLY_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Update/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    ),
                                    getExceptionResponseFiels()
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
                    .updateStudyWeekly(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEK)
                    .file((MockMultipartFile) files1)
                    .file((MockMultipartFile) files2)
                    .file((MockMultipartFile) files3)
                    .file((MockMultipartFile) files4)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("title", STUDY_WEEKLY_1.getTitle())
                    .queryParam("content", STUDY_WEEKLY_1.getContent())
                    .queryParam("startDate", STUDY_WEEKLY_1.getPeriod().getStartDate().format(DATE_TIME_FORMATTER))
                    .queryParam("endDate", STUDY_WEEKLY_1.getPeriod().getEndDate().format(DATE_TIME_FORMATTER))
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
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("수정할 주차")
                                    ),
                                    requestParts(
                                            partWithName("files").description("스터디 해당 주차에 대한 첨부파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("title").description("스터디 주차 제목"),
                                            parameterWithName("content").description("스터디 주차 내용"),
                                            parameterWithName("startDate").description("스터디 주차 시작 날짜"),
                                            parameterWithName("endDate").description("스터디 주차 종료 날짜"),
                                            parameterWithName("assignmentExists").description("스터디 주차 과제 존재 여부"),
                                            parameterWithName("autoAttendance").description("스터디 주차 자동 출석 여부")
                                                    .attributes(constraint("과제 존재 여부가 false면 자동 출석은 무조건 false"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차 삭제 API [DELETE /api/studies/{studyId}/weeks/{week}] - AccessToken 필수")
    class deleteWeekly {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{week}";
        private static final Integer WEEK = 1;
        private static final Long STUDY_ID = 1L;
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
                    .delete(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Delete/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("삭제할 주차")
                                    ),
                                    getExceptionResponseFiels()
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
                    .deleteStudyWeekly(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.ONLY_LATEST_WEEKLY_CAN_DELETE;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Weekly/Delete/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("삭제할 주차")
                                    ),
                                    getExceptionResponseFiels()
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
                    .delete(BASE_URL, STUDY_ID, WEEK)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

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
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("week").description("삭제할 주차")
                                    )
                            )
                    );
        }
    }
}
