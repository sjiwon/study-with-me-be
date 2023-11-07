package com.kgu.studywithme.favorite.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Favorite -> FavoriteApiController 테스트")
class FavoriteApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("찜 등록 API [POST /api/studies/{studyId}/like] - AccessToken 필수")
    class LikeMarking {
        private static final String BASE_URL = "/api/studies/{studyId}/like";
        private static final Long STUDY_ID = 1L;
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("이미 찜 등록된 스터디를 중복으로 찜할 수 없다")
        void throwExceptionByAlreadyLikeMarked() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doThrow(StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED))
                    .when(manageFavoriteUseCase)
                    .markLike(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.ALREADY_LIKE_MARKED;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Favorite/LikeMarking/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("찜 등록 할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("해당 스터디를 찜 등록한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(manageFavoriteUseCase.markLike(any())).willReturn(1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Favorite/LikeMarking/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("찜 등록 할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("찜 취소 API [DELETE /api/studies/{studyId}/like] - AccessToken 필수")
    class LikeCancellation {
        private static final String BASE_URL = "/api/studies/{studyId}/like";
        private static final Long STUDY_ID = 1L;
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("해당 스터디에 대해서 찜한 기록이 없다면 취소할 수 없다")
        void throwExceptionByFavoriteRecordNotFound() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doThrow(StudyWithMeException.type(FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND))
                    .when(manageFavoriteUseCase)
                    .cancelLike(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            final FavoriteErrorCode expectedError = FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Favorite/LikeCancellation/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("찜 취소 할 스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("해당 스터디에 대해서 등록한 찜을 취소한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doNothing()
                    .when(manageFavoriteUseCase)
                    .cancelLike(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Favorite/LikeCancellation/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("찜 취소 할 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
