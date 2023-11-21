package com.kgu.studywithme.studyweekly.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
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

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StudyWeekly -> StudyWeeklySubmitApiController 테스트")
class StudyWeeklySubmitApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 주차별 과제 제출 API [POST /api/studies/{studyId}/weeks/{weeklyId}/assignment] - AccessToken 필수")
    class SubmitWeeklyAssignment {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{weeklyId}/assignment";
        private static final Long STUDY_ID = 1L;
        private static final Long WEEKLY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private MultipartFile file;

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);

            file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 해당 주차에 과제를 제출할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file");

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentSubmit/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 제출이 불가능하다")
        void throwExceptionByMissingSubmission() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION))
                    .when(submitWeeklyAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.MISSING_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentSubmit/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 제출이 불가능하다")
        void throwExceptionByDuplicateSubmission() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION))
                    .when(submitWeeklyAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file")
                    .queryParam("link", "https://notion.so");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.DUPLICATE_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentSubmit/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("제출한 타입[link/file]에 대해서 실제 제출한 양식[링크/파일]이 매칭이 되지 않음에 따라 제출이 불가능하다")
        void throwExceptionByInvalidBetweenSubmitTypAndResult() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT))
                    .when(submitWeeklyAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "link");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentSubmit/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("해당 주차 과제를 제출한다 [파일 제출]")
        void successWithFile() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentSubmit/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("해당 주차 과제를 제출한다 [링크 제출]")
        void successWithLink() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "link")
                    .queryParam("link", "https://notion.so");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentSubmit/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 주차별 제출한 과제 수정 API [POST /api/studies/{studyId}/weeks/{weeklyId}/assignment/edit] - AccessToken 필수")
    class EditSubmittedWeeklyAssignment {
        private static final String BASE_URL = "/api/studies/{studyId}/weeks/{weeklyId}/assignment/edit";
        private static final Long STUDY_ID = 1L;
        private static final Long WEEKLY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private MultipartFile file;

        @BeforeEach
        void setUp() throws IOException {
            mockingForStudyParticipant(STUDY_ID, HOST_ID, true);
            mockingForStudyParticipant(STUDY_ID, ANONYMOUS_ID, false);

            file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        }

        @Test
        @DisplayName("스터디 참여자가 아니라면 해당 주차에 제출한 과제를 수정할 수 없다")
        void throwExceptionByMemberIsNotParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "link")
                    .queryParam("link", "https://notion.so");

            // then
            final StudyParticipantErrorCode expectedError = StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentEdit/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("제출한 과제를 수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 제출한 과제 수정에 실패한다")
        void throwExceptionByMissingSubmission() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION))
                    .when(editWeeklySubmittedAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.MISSING_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentEdit/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("제출한 과제를 수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 제출한 과제 수정에 실패한다")
        void throwExceptionByDuplicateSubmission() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION))
                    .when(editWeeklySubmittedAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file")
                    .queryParam("link", "https://notion.so");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.DUPLICATE_SUBMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentEdit/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("제출한 과제를 수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("제출한 타입[link/file]에 대해서 실제 제출한 양식[링크/파일]이 매칭이 되지 않음에 따라 제출한 과제 수정에 실패한다")
        void throwExceptionByInvalidBetweenSubmitTypAndResult() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT))
                    .when(editWeeklySubmittedAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "link");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentEdit/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("과제를 제출할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("이전에 제출한 과제가 없다면 수정할 수 없다")
        void throwExceptionBySubmittedAssignmentNotFound() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND))
                    .when(editWeeklySubmittedAssignmentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "file")
                    .queryParam("link", "https://notion.so");

            // then
            final StudyWeeklyErrorCode expectedError = StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentEdit/Failure/Case5",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("제출한 과제를 수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("제출한 과제를 수정한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, STUDY_ID, WEEKLY_ID)
                    .header(AUTHORIZATION, applyAccessToken())
                    .queryParam("type", "link")
                    .queryParam("link", "https://notion.so");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Weekly/AssignmentEdit/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)"),
                                            parameterWithName("weeklyId")
                                                    .description("제출한 과제를 수정할 주차 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("file")
                                                    .description("제출할 파일")
                                                    .optional()
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("과제 제출 타입")
                                                    .attributes(constraint("file=파일 / link=링크")),
                                            parameterWithName("link")
                                                    .description("제출할 링크")
                                                    .optional()
                                    )
                            )
                    );
        }
    }
}
