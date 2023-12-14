package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kgu.studywithme.auth.utils.TokenResponseWriter.REFRESH_TOKEN_COOKIE;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithRefreshToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyRefreshToken;
import static com.kgu.studywithme.common.utils.TokenUtils.createTokenResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth -> TokenReissueApiController 테스트")
class TokenReissueApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("토큰 재발급 API [POST /api/token/reissue] - RefreshToken 필수")
    class ReissueToken {
        private static final String BASE_URL = "/api/token/reissue";

        @Test
        @DisplayName("만료된 RefreshToken으로 인해 토큰 재발급에 실패한다")
        void throwExceptionByExpiredRefreshToken() throws Exception {
            // given
            mockingTokenWithExpiredException();

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE, applyRefreshToken()));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.EXPIRED_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "TokenReissueApi/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithRefreshToken(),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("이미 사용했거나 조작된 RefreshToken이면 토큰 재발급에 실패한다")
        void throwExceptionByInvalidRefreshToken() throws Exception {
            // given
            mockingTokenWithInvalidException();

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE, applyRefreshToken()));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "TokenReissueApi/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithRefreshToken(),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void success() throws Exception {
            // given
            mockingToken(true, 1L);
            given(reissueTokenUseCase.invoke(any())).willReturn(createTokenResponse());

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .cookie(new Cookie(REFRESH_TOKEN_COOKIE, applyRefreshToken()));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isNoContent())
                    .andDo(
                            document(
                                    "TokenReissueApi/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithRefreshToken(),
                                    responseHeaders(
                                            headerWithName(AUTHORIZATION)
                                                    .description("Access Token"),
                                            headerWithName(SET_COOKIE)
                                                    .description("Set Refresh Token")
                                    ),
                                    responseCookies(
                                            cookieWithName(REFRESH_TOKEN_COOKIE)
                                                    .description("Refresh Token")
                                    )
                            )
                    );
        }
    }
}
