package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.dto.LoginResponse;
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

import java.util.UUID;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth -> OAuthApiController 테스트")
class OAuthApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access/{provider}]")
    class getAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access/{provider}";
        private static final String PROVIDER_GOOGLE = "google";
        private static final String REDIRECT_URL = "http://localhost:3000";

        @Test
        @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
        void throwExceptionByInvalidOAuthProvider() throws Exception {
            // given
            doThrow(StudyWithMeException.type(AuthErrorCode.INVALID_OAUTH_PROVIDER))
                    .when(queryOAuthLinkUseCase)
                    .queryOAuthLink(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, PROVIDER_GOOGLE)
                    .param("redirectUrl", REDIRECT_URL);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_OAUTH_PROVIDER;
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
                                    "OAuthApi/Access/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("provider")
                                                    .description("OAuth Provider")
                                                    .attributes(constraint("google / kakao / ..."))
                                    ),
                                    queryParameters(
                                            parameterWithName("redirectUrl")
                                                    .description("Authorization Code와 함께 redirect될 URI")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("Google OAuth Authorization Code 요청을 위한 URI를 생성한다")
        void successGoogle() throws Exception {
            // given
            given(queryOAuthLinkUseCase.queryOAuthLink(any()))
                    .willReturn("https://url-for-authorization-code");

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, PROVIDER_GOOGLE)
                    .param("redirectUrl", REDIRECT_URL);

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
                                            parameterWithName("redirectUrl")
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
    class oAuthLogin {
        private static final String BASE_URL = "/api/oauth/login/{provider}";
        private static final String PROVIDER_GOOGLE = "google";
        private static final OAuthLoginRequest REQUEST = new OAuthLoginRequest(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "http://localhost:3000"
        );

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() throws Exception {
            // given
            final GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();
            doThrow(new StudyWithMeOAuthException(googleUserResponse))
                    .when(oAuthLoginUseCase)
                    .login(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, PROVIDER_GOOGLE)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.name").exists(),
                            jsonPath("$.name").value(googleUserResponse.getName()),
                            jsonPath("$.email").exists(),
                            jsonPath("$.email").value(googleUserResponse.getEmail()),
                            jsonPath("$.profileImage").exists(),
                            jsonPath("$.profileImage").value(googleUserResponse.getProfileImage())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("authorizationCode")
                                                    .description("Authorization Code"),
                                            fieldWithPath("redirectUrl").
                                                    description("redirectUrl")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUrl과 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("name")
                                                    .description("회원가입 진행 시 이름 기본값 [Read-Only]"),
                                            fieldWithPath("email")
                                                    .description("회원가입 진행 시 이메일 기본값 [Read-Only]"),
                                            fieldWithPath("profileImage")
                                                    .description("회원가입 진행 시 구글 프로필 이미지 기본값 [Read-Only]")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() throws Exception {
            // given
            final LoginResponse loginResponse = JIWON.toLoginResponse();
            given(oAuthLoginUseCase.login(any())).willReturn(loginResponse);

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
                            jsonPath("$.accessToken").exists(),
                            jsonPath("$.accessToken").value(loginResponse.accessToken()),
                            jsonPath("$.refreshToken").exists(),
                            jsonPath("$.refreshToken").value(loginResponse.refreshToken())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("authorizationCode")
                                                    .description("Authorization Code"),
                                            fieldWithPath("redirectUrl")
                                                    .description("redirectUrl")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUrl과 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("member.id")
                                                    .description("사용자 ID(PK)"),
                                            fieldWithPath("member.nickname")
                                                    .description("사용자 닉네임"),
                                            fieldWithPath("member.email")
                                                    .description("사용자 이메일"),
                                            fieldWithPath("accessToken")
                                                    .description("발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken")
                                                    .description("발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/oauth/logout] - AccessToken 필수")
    class logout {
        private static final String BASE_URL = "/api/oauth/logout";

        @Test
        @DisplayName("로그아웃을 진행한다")
        void success() throws Exception {
            // given
            mockingToken(true, 1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

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
