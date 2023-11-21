package com.kgu.studywithme.studyparticipant.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
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
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyParticipant -> StudyFinalizeApiController 테스트")
class StudyFinalizeApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 참여 취소 API [PATCH /api/studies/{studyId}/participants/leave] - AccessToken 필수")
    class Leave {
        private static final String BASE_URL = "/api/studies/{studyId}/participants/leave";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, PARTICIPANT_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 스터디를 떠날 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Leave/Failure/Case1",
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
        @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않으면 스터디를 떠날 수 없다")
        void throwExceptionByHostCannotLeaveStudy() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY))
                    .when(leaveStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Leave/Failure/Case2",
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
        @DisplayName("스터디를 떠난다")
        void success() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doNothing()
                    .when(leaveStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Leave/Success",
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
    @DisplayName("스터디 졸업 API [PATCH /api/studies/{studyId}/graduate] - AccessToken 필수")
    class Graduate {
        private static final String BASE_URL = "/api/studies/{studyId}/graduate";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;

        @BeforeEach
        void setUp() {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, PARTICIPANT_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 스터디를 졸업할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Graduate/Failure/Case1",
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
        @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않으면 스터디를 졸업할 수 없다")
        void throwExceptionByHostCannotGraduateStudy() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY))
                    .when(graduateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Graduate/Failure/Case2",
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
        @DisplayName("졸업 요건을 만족하지 못한 참여자는 스터디를 졸업할 수 없다")
        void throwExceptionByParticipantNotMeetGraduationPolicy() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doThrow(StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY))
                    .when(graduateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Participation/Graduate/Failure/Case3",
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
        @DisplayName("스터디를 졸업한다")
        void success() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doNothing()
                    .when(graduateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessToken());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Participation/Graduate/Success",
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
