package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyparticipant.presentation.dto.request.RejectParticipationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyParticipant -> StudyParticipantDecisionApiController 테스트")
class StudyParticipantDecisionApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 참여 승인 API [PATCH /api/studies/{studyId}/applicants/{applierId}/approve] - AccessToken 필수")
    class approve {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants/{applierId}/approve";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long APPLIER_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, APPLIER_ID, false);
        }

        @Test
        @DisplayName("스터디 팀장이 아니면 참여 승인 권한이 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, APPLIER_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
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
                                    "StudyApi/Participation/Approve/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 신청자가 아닌 사용자에 대해서 참여 승인을 할 수 없다")
        void throwExceptionByApplierNotFound() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND))
                    .when(approveParticipationUseCase)
                    .approveParticipation(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.APPLIER_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Participation/Approve/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료됨에 따라 참여 승인을 할 수 없다")
        void throwExceptionByStudyIsEnd() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.STUDY_IS_TERMINATED))
                    .when(approveParticipationUseCase)
                    .approveParticipation(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.STUDY_IS_TERMINATED;
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
                                    "StudyApi/Participation/Approve/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 정원이 꽉 찼기 때문에 추가적인 참여 승인을 할 수 없다")
        void throwExceptionByStudyCapacityIsFull() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.STUDY_CAPACITY_ALREADY_FULL))
                    .when(approveParticipationUseCase)
                    .approveParticipation(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.STUDY_CAPACITY_ALREADY_FULL;
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
                                    "StudyApi/Participation/Approve/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 승인에 성공한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doNothing()
                    .when(approveParticipationUseCase)
                    .approveParticipation(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Approve/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여 거절 API [PATCH /api/studies/{studyId}/applicants/{applierId}/reject] - AccessToken 필수")
    class reject {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants/{applierId}/reject";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long APPLIER_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID, true);
            mockingForStudyHost(STUDY_ID, APPLIER_ID, false);
        }

        @Test
        @DisplayName("스터디 팀장이 아니면 참여 거절 권한이 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, APPLIER_ID);

            // when
            final RejectParticipationRequest request = new RejectParticipationRequest("열정 온도가 너무 낮아요 ㅠ");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

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
                                    "StudyApi/Participation/Reject/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 승인할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("거절 사유를 적지 않으면 참여 신청을 거절할 수 없다")
        void throwExceptionByRejectReasonIsEmpty() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED))
                    .when(rejectParticipationUseCase)
                    .rejectParticipation(any());

            // when
            final RejectParticipationRequest request = new RejectParticipationRequest("");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "참여 거절 사유는 필수입니다.";
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(message)
                    )
                    .andDo(
                            document(
                                    "StudyApi/Participation/Reject/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 신청자가 아닌 사용자에 대해서 참여 거절을 할 수 없다")
        void throwExceptionByApplierNotFound() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND))
                    .when(rejectParticipationUseCase)
                    .rejectParticipation(any());

            // when
            final RejectParticipationRequest request = new RejectParticipationRequest("열정 온도가 너무 낮아요 ㅠ");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.APPLIER_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Participation/Reject/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("스터디가 종료됨에 따라 참여 거절을 할 수 없다")
        void throwExceptionByStudyIsEnd() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.STUDY_IS_TERMINATED))
                    .when(rejectParticipationUseCase)
                    .rejectParticipation(any());

            // when
            final RejectParticipationRequest request = new RejectParticipationRequest("열정 온도가 너무 낮아요 ㅠ");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.STUDY_IS_TERMINATED;
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
                                    "StudyApi/Participation/Reject/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("참여 거절을 성공한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doNothing()
                    .when(rejectParticipationUseCase)
                    .rejectParticipation(any());

            // when
            final RejectParticipationRequest request = new RejectParticipationRequest("열정 온도가 너무 낮아요 ㅠ");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, APPLIER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Reject/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId").description("스터디 ID(PK)"),
                                            parameterWithName("applierId").description("참여 거절할 사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("참여 거절 사유")
                                    )
                            )
                    );
        }
    }
}
