package com.kgu.studywithme.memberreview.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.memberreview.presentation.dto.request.UpdateMemberReviewRequest;
import com.kgu.studywithme.memberreview.presentation.dto.request.WriteMemberReviewRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

@DisplayName("MemberReview -> MemberReviewApiController 테스트")
class MemberReviewApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 리뷰 작성 API [POST /api/members/{revieweeId}/review] - AccessToken 필수")
    class writeMemberReview {
        private static final String BASE_URL = "/api/members/{revieweeId}/review";
        private static final Long REVIEWEE_ID = 1L;
        private static final Long REVIEWER_ID = 2L;

        @Test
        @DisplayName("본인에게 리뷰를 남길 수 없다")
        void throwExceptionBySelfReviewNotAllowed() throws Exception {
            // given
            mockingToken(true, REVIEWEE_ID);
            doThrow(StudyWithMeException.type(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED))
                    .when(writeMemberReviewUseCase)
                    .writeMemberReview(any());

            // when
            final WriteMemberReviewRequest request = new WriteMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberReviewErrorCode expectedError = MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/Review/Write/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 작성 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("리뷰 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("함께 스터디를 진행한 기록이 없다면 리뷰를 남길 수 없다")
        void throwExceptionByCommonStudyRecordNotFound() throws Exception {
            // given
            mockingToken(true, REVIEWER_ID);
            doThrow(StudyWithMeException.type(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND))
                    .when(writeMemberReviewUseCase)
                    .writeMemberReview(any());

            // when
            final WriteMemberReviewRequest request = new WriteMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberReviewErrorCode expectedError = MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND;
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
                                    "MemberApi/Review/Write/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 작성 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("리뷰 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("해당 사용자에 대해 2번 이상 리뷰를 남길 수 없다")
        void throwExceptionByAlreadyReview() throws Exception {
            // given
            mockingToken(true, REVIEWER_ID);
            doThrow(StudyWithMeException.type(MemberReviewErrorCode.ALREADY_REVIEW))
                    .when(writeMemberReviewUseCase)
                    .writeMemberReview(any());

            // when
            final WriteMemberReviewRequest request = new WriteMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberReviewErrorCode expectedError = MemberReviewErrorCode.ALREADY_REVIEW;
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
                                    "MemberApi/Review/Write/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 작성 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("리뷰 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("리뷰 작성에 성공한다")
        void success() throws Exception {
            // given
            mockingToken(true, REVIEWER_ID);
            given(writeMemberReviewUseCase.writeMemberReview(any())).willReturn(1L);

            // when
            final WriteMemberReviewRequest request = new WriteMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Review/Write/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 작성 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("리뷰 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 리뷰 수정 API [PATCH /api/members/{revieweeId}/review] - AccessToken 필수")
    class updateMemberReview {
        private static final String BASE_URL = "/api/members/{revieweeId}/review";
        private static final Long REVIEWEE_ID = 1L;
        private static final Long REVIEWER_ID = 2L;

        @Test
        @DisplayName("작성한 리뷰가 없다면 수정할 수 없다")
        void throwExceptionByMemberReviewNotFound() throws Exception {
            // given
            mockingToken(true, REVIEWER_ID);
            doThrow(StudyWithMeException.type(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND))
                    .when(updateMemberReviewUseCase)
                    .updateMemberReview(any());

            // when
            final UpdateMemberReviewRequest request = new UpdateMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberReviewErrorCode expectedError = MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND;
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
                                    "MemberApi/Review/Update/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 수정 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("수정할 리뷰 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("이전과 동일한 내용으로 리뷰를 수정할 수 없다")
        void throwExceptionByContentSameAsBefore() throws Exception {
            // given
            mockingToken(true, REVIEWER_ID);
            doThrow(StudyWithMeException.type(MemberReviewErrorCode.CONTENT_SAME_AS_BEFORE))
                    .when(updateMemberReviewUseCase)
                    .updateMemberReview(any());

            // when
            final UpdateMemberReviewRequest request = new UpdateMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberReviewErrorCode expectedError = MemberReviewErrorCode.CONTENT_SAME_AS_BEFORE;
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
                                    "MemberApi/Review/Update/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 수정 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("수정할 리뷰 내용")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("리뷰 수정에 성공한다")
        void success() throws Exception {
            // given
            mockingToken(true, REVIEWER_ID);
            doNothing()
                    .when(updateMemberReviewUseCase)
                    .updateMemberReview(any());

            // when
            final UpdateMemberReviewRequest request = new UpdateMemberReviewRequest("스터디에 참여를 잘해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, REVIEWEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Review/Update/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("revieweeId").description("리뷰 수정 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("content").description("수정할 리뷰 내용")
                                    )
                            )
                    );
        }
    }
}
