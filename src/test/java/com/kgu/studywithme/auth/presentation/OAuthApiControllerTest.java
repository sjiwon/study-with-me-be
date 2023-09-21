package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.kgu.studywithme.common.utils.OAuthUtils.GOOGLE_PROVIDER;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static com.kgu.studywithme.common.utils.OAuthUtils.STATE;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessTokenToAuthorizationHeader;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth -> OAuthApiController 테스트")
class OAuthApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access/{provider}]")
    class GetAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access/{provider}";

        @Test
        @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
        void throwExceptionByInvalidOAuthProvider() throws Exception {
            // given
            doThrow(StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER))
                    .when(getOAuthLinkUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, GOOGLE_PROVIDER)
                    .param("redirectUri", REDIRECT_URI);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_OAUTH_PROVIDER;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "OAuthApi/Access/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("provider")
                                                    .description("OAuth Provider")
                                                    .attributes(constraint("google / kakao / ..."))
                                    ),
                                    queryParameters(
                                            parameterWithName("redirectUri")
                                                    .description("Authorization Code와 함께 redirect될 URI")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("Google OAuth Authorization Code 요청을 위한 URI를 생성한다")
        void successGoogle() throws Exception {
            // given
            given(getOAuthLinkUseCase.invoke(any())).willReturn("https://url-for-authorization-code");

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, GOOGLE_PROVIDER)
                    .param("redirectUri", REDIRECT_URI);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value("https://url-for-authorization-code")
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Access/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("provider")
                                                    .description("OAuth Provider")
                                                    .attributes(constraint("google / kakao / ..."))
                                    ),
                                    queryParameters(
                                            parameterWithName("redirectUri")
                                                    .description("Authorization Code와 함께 redirect될 URI")
                                    ),
                                    responseFields(
                                            fieldWithPath("result")
                                                    .description("Authorization Code 요청을 위한 URI")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API [POST /api/oauth/login/{provider}]")
    class OAuthLogin {
        private static final String BASE_URL = "/api/oauth/login/{provider}";
        private static final String PROVIDER_GOOGLE = "google";
        private static final OAuthLoginRequest REQUEST = new OAuthLoginRequest(AUTHORIZATION_CODE, REDIRECT_URI, STATE);

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() throws Exception {
            // given
            final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();
            doThrow(new StudyWithMeOAuthException(googleUserResponse))
                    .when(oAuthLoginUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, PROVIDER_GOOGLE)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.name").exists(),
                            jsonPath("$.name").value(googleUserResponse.name()),
                            jsonPath("$.email").exists(),
                            jsonPath("$.email").value(googleUserResponse.email())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("authorizationCode")
                                                    .description("Authorization Code"),
                                            fieldWithPath("redirectUri").
                                                    description("Redirect Uri")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUri와 반드시 동일한 값")),
                                            fieldWithPath("state")
                                                    .description("State 값 (CSRF 공격 방지용)")
                                                    .attributes(constraint("Authorization Code 요청 시 state와 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("name")
                                                    .description("회원가입 진행 시 이름 기본값 [Read-Only]"),
                                            fieldWithPath("email")
                                                    .description("회원가입 진행 시 이메일 기본값 [Read-Only]")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() throws Exception {
            // given
            final AuthMember loginResponse = JIWON.toAuthMember();
            given(oAuthLoginUseCase.invoke(any())).willReturn(loginResponse);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, PROVIDER_GOOGLE)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.member.id").exists(),
                            jsonPath("$.member.id").value(loginResponse.member().id()),
                            jsonPath("$.member.nickname").exists(),
                            jsonPath("$.member.nickname").value(loginResponse.member().nickname()),
                            jsonPath("$.member.email").exists(),
                            jsonPath("$.member.email").value(loginResponse.member().email()),
                            jsonPath("$.token.accessToken").exists(),
                            jsonPath("$.token.accessToken").value(loginResponse.token().accessToken()),
                            jsonPath("$.token.refreshToken").exists(),
                            jsonPath("$.token.refreshToken").value(loginResponse.token().refreshToken())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("authorizationCode")
                                                    .description("Authorization Code"),
                                            fieldWithPath("redirectUri").
                                                    description("Redirect Uri")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUri와 반드시 동일한 값")),
                                            fieldWithPath("state")
                                                    .description("State 값 (CSRF 공격 방지용)")
                                                    .attributes(constraint("Authorization Code 요청 시 state와 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("member.id")
                                                    .description("사용자 ID(PK)"),
                                            fieldWithPath("member.nickname")
                                                    .description("사용자 닉네임"),
                                            fieldWithPath("member.email")
                                                    .description("사용자 이메일"),
                                            fieldWithPath("token.accessToken")
                                                    .description("발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("token.refreshToken")
                                                    .description("발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/oauth/logout] - AccessToken 필수")
    class Logout {
        private static final String BASE_URL = "/api/oauth/logout";

        @Test
        @DisplayName("로그아웃을 진행한다")
        void success() throws Exception {
            // given
            mockingToken(true, 1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "OAuthApi/Logout",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken()
                            )
                    );
        }
    }
}
