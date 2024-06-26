package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyParticipant -> StudyApplyApiController 테스트")
class StudyApplyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 참여 신청 API [POST /api/studies/{studyId}/applicants] - AccessToken 필수")
    class Apply {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long MEMBER_ID = 2L;
        private static final Long PARTICIPANT_ID = 3L;

        @Test
        @DisplayName("스터디가 모집중이지 않으면 참여 신청을 할 수 없다")
        void throwExceptionByStudyIsNotRecruitingNow() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW))
                    .when(applyStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyErrorCode expectedError = StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Failure/Case1",
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
        @DisplayName("스터디 팀장은 본인 스터디에 참여 신청을 할 수 없다")
        void throwExceptionByStudyHostCannotApplyOwnStudy() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY))
                    .when(applyStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Failure/Case2",
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
        @DisplayName("이미 신청했거나 참여중인 스터디에 다시 참여 신청할 수 없다")
        void throwExceptionByAlreadyApplyOrParticipate() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE))
                    .when(applyStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Failure/Case3",
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
        @DisplayName("스터디 참여를 취소했거나 졸업한 사람은 동일 스터디에 다시 참여 신청을 할 수 없다")
        void throwExceptionByAlreadyLeaveOrGraduated() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED))
                    .when(applyStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Failure/Case4",
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
        @DisplayName("스터디에 참여 신청을 한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(applyStudyUseCase.invoke(any())).willReturn(1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Apply/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여 신청 취소 API [DELETE /api/studies/{studyId}/applicants] - AccessToken 필수")
    class ApplyCancel {
        private static final String BASE_URL = "/api/studies/{studyId}/applicants";
        private static final Long STUDY_ID = 1L;
        private static final Long APPLIER_ID = 1L;
        private static final Long ANONYMOUS_ID = 1L;

        @Test
        @DisplayName("스터디 신청자가 아니면 신청 취소를 할 수 없다")
        void throwExceptionByRequesterIsNotApplier() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND))
                    .when(applyCancelUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.APPLIER_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/ApplyCancel/Failure",
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
        @DisplayName("스터디 참여 신청한 내역을 취소한다")
        void success() throws Exception {
            // given
            mockingToken(true, APPLIER_ID);
            doNothing()
                    .when(applyCancelUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/ApplyCancel/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
