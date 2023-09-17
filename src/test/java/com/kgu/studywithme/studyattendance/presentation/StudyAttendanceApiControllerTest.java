package com.kgu.studywithme.studyattendance.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import com.kgu.studywithme.studyattendance.presentation.dto.request.ManualAttendanceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessTokenToAuthorizationHeader;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyAttendance -> StudyAttendanceApiController 테스트")
class StudyAttendanceApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("수동 출석 API [PATCH /api/studies/{studyId}/attendance/{memberId}] - AccessToken 필수")
    class ManualCheckAttendance {
        private static final String BASE_URL = "/api/studies/{studyId}/attendance/{memberId}";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;
        private static final ManualAttendanceRequest REQUEST = new ManualAttendanceRequest(1, ATTENDANCE.getValue());

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID);
        }

        @Test
        @DisplayName("팀장이 아니라면 수동으로 출석 체크를 진행할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("memberId")
                                                    .description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week")
                                                    .description("수동 출석할 주차"),
                                            fieldWithPath("status")
                                                    .description("출석 정보")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("미출석으로 출석 체크를 진행할 수 없다")
        void throwExceptionByCannotUpdateToNonAttendance() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final ManualAttendanceRequest request = new ManualAttendanceRequest(1, NON_ATTENDANCE.getValue());
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, ANONYMOUS_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "출석을 미출결로 수정할 수 없습니다.";
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError, message))
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("memberId")
                                                    .description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week")
                                                    .description("수동 출석할 주차"),
                                            fieldWithPath("status")
                                                    .description("출석 정보")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("해당 주차에 출석 정보가 존재하지 않는다면 출석 체크를 진행할 수 없다")
        void throwExceptionByAttendanceNotFound() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyAttendanceErrorCode.ATTENDANCE_NOT_FOUND))
                    .when(manualAttendanceUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, ANONYMOUS_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            final StudyAttendanceErrorCode expectedError = StudyAttendanceErrorCode.ATTENDANCE_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("memberId")
                                                    .description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week")
                                                    .description("수동 출석할 주차"),
                                            fieldWithPath("status")
                                                    .description("출석 정보")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("수동 출석 체크를 진행한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doNothing()
                    .when(manualAttendanceUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("memberId")
                                                    .description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week")
                                                    .description("수동 출석할 주차"),
                                            fieldWithPath("status")
                                                    .description("출석 정보")
                                    )
                            )
                    );
        }
    }
}
