package com.kgu.studywithme.studynotice.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import com.kgu.studywithme.studynotice.presentation.dto.request.UpdateStudyNoticeCommentRequest;
import com.kgu.studywithme.studynotice.presentation.dto.request.WriteStudyNoticeCommentRequest;
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

@DisplayName("StudyNotice/Comment -> StudyNoticeCommentApiController 테스트")
class StudyNoticeCommentApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("공지사항 댓글 작성 API [POST /api/notices/{noticeId}/comment] - AccessToken 필수")
    class Write {
        private static final String BASE_URL = "/api/notices/{noticeId}/comment";
        private static final Long NOTICE_ID = 1L;
        private static final Long PARTICIPANT_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private static final WriteStudyNoticeCommentRequest REQUEST = new WriteStudyNoticeCommentRequest("공지사항 댓글~~");

        @Test
        @DisplayName("스터디 참여자가 아니면 공지사항에 댓글을 작성할 수 없다")
        void throwExceptionByWriterIsNotStudyParticipant() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT))
                    .when(writeStudyNoticeCommentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, NOTICE_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            final StudyNoticeErrorCode expectedError = StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Notice/Comment/Write/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId")
                                                    .description("공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .description("댓글 내용")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항에 댓글을 작성한다")
        void success() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doNothing()
                    .when(writeStudyNoticeCommentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, NOTICE_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Notice/Comment/Write/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId")
                                                    .description("공지사항 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .description("댓글 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 수정 API [PUT /api/notices/{noticeId}/comments/{commentId}] - AccessToken 필수")
    class Update {
        private static final String BASE_URL = "/api/notices/{noticeId}/comments/{commentId}";
        private static final Long NOTICE_ID = 1L;
        private static final Long COMMENT_ID = 1L;
        private static final Long PARTICIPANT_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private static final UpdateStudyNoticeCommentRequest REQUEST = new UpdateStudyNoticeCommentRequest("공지사항 댓글~~");

        @Test
        @DisplayName("댓글 작성자가 아니면 수정할 수 없다")
        void throwExceptionByMemberIsNotWriter() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT))
                    .when(updateStudyNoticeCommentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, NOTICE_ID, COMMENT_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            final StudyNoticeErrorCode expectedError = StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Notice/Comment/Update/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId")
                                                    .description("공지사항 ID(PK)"),
                                            parameterWithName("commentId")
                                                    .description("수정할 댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .description("수정할 댓글 내용")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항 댓글을 수정한다")
        void success() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doNothing()
                    .when(updateStudyNoticeCommentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, NOTICE_ID, COMMENT_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Notice/Comment/Update/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId")
                                                    .description("공지사항 ID(PK)"),
                                            parameterWithName("commentId")
                                                    .description("수정할 댓글 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .description("수정할 댓글 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("공지사항 댓글 삭제 API [DELETE /api/notices/{noticeId}/comments/{commentId}] - AccessToken 필수")
    class Delete {
        private static final String BASE_URL = "/api/notices/{noticeId}/comments/{commentId}";
        private static final Long NOTICE_ID = 1L;
        private static final Long COMMENT_ID = 1L;
        private static final Long PARTICIPANT_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @Test
        @DisplayName("댓글 작성자가 아니면 삭제할 수 없다")
        void throwExceptionByMemberIsNotWriter() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);
            doThrow(StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT))
                    .when(deleteStudyNoticeCommentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, NOTICE_ID, COMMENT_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            final StudyNoticeErrorCode expectedError = StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Notice/Comment/Delete/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId")
                                                    .description("공지사항 ID(PK)"),
                                            parameterWithName("commentId")
                                                    .description("삭제할 댓글 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("공지사항 댓글을 삭제한다")
        void success() throws Exception {
            // given
            mockingToken(true, PARTICIPANT_ID);
            doNothing()
                    .when(deleteStudyNoticeCommentUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, NOTICE_ID, COMMENT_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Notice/Comment/Delete/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("noticeId")
                                                    .description("공지사항 ID(PK)"),
                                            parameterWithName("commentId")
                                                    .description("삭제할 댓글 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
