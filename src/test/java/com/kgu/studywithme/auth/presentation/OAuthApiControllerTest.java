package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthProperties;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;
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

@DisplayName("Auth [Presentation Layer] -> OAuthApiController 테스트")
class OAuthApiControllerTest extends ControllerTest {
    @MockBean
    private GoogleOAuthProperties properties;

    @BeforeEach
    void setUp() {
        given(properties.getAuthUrl()).willReturn("https://accounts.google.com/o/oauth2/v2/auth");
        given(properties.getClientId()).willReturn("client_id");
        given(properties.getScope()).willReturn(Set.of("openid", "profile", "email"));
    }

    @Nested
    @DisplayName("OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access/{provider}]")
    class getAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access/{provider}";
        private static final String PROVIDER_GOOGLE = "google";
        private static final String REDIRECT_URL = "http://localhost:3000";

        @Test
        @DisplayName("Google OAuth Authorization Code 요청을 위한 URI를 생성한다")
        void googleSuccess() throws Exception {
            // given
            String authorizationCodeRequestUri = generateAuthorizationCodeRequestUri(REDIRECT_URL);
            given(queryOAuthLinkUseCase.createOAuthLink(any())).willReturn(authorizationCodeRequestUri);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, PROVIDER_GOOGLE)
                    .param("redirectUrl", REDIRECT_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value(authorizationCodeRequestUri)
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Access",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("provider").description("OAuth Provider")
                                                    .attributes(constraint("google / kakao / ..."))
                                    ),
                                    queryParameters(
                                            parameterWithName("redirectUrl").description("Authorization Code와 함께 redirect될 URI")
                                    ),
                                    responseFields(
                                            fieldWithPath("result").description("Authorization Code 요청을 위한 URI")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API [POST /api/oauth/login]")
    class oAuthLogin {
        private static final String BASE_URL = "/api/oauth/login";
        private static final String AUTHORIZATION_CODE = UUID.randomUUID().toString().replaceAll("-", "").repeat(2);
        private static final String REDIRECT_URL = "http://localhost:3000";

        @Test
        @DisplayName("Google 이메일에 해당하는 사용자가 DB에 존재하지 않을 경우 예외가 발생하고 추가정보 기입을 통해서 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() throws Exception {
            // given
            GoogleUserResponse googleUserResponse = JIWON.toGoogleUserResponse();
            doThrow(new StudyWithMeOAuthException(googleUserResponse))
                    .when(oAuthService)
                    .login(AUTHORIZATION_CODE, REDIRECT_URL);

            // when
            final OAuthLoginRequest request
                    = new OAuthLoginRequest(AUTHORIZATION_CODE, REDIRECT_URL);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.name").exists(),
                            jsonPath("$.name").value(googleUserResponse.name()),
                            jsonPath("$.email").exists(),
                            jsonPath("$.email").value(googleUserResponse.email()),
                            jsonPath("$.picture").exists(),
                            jsonPath("$.picture").value(googleUserResponse.picture())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("authorizationCode").description("Authorization Code"),
                                            fieldWithPath("redirectUrl").description("redirectUrl")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUrl과 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("name").description("회원가입 진행 시 이름 기본값 [Read-Only]"),
                                            fieldWithPath("email").description("회원가입 진행 시 이메일 기본값 [Read-Only]"),
                                            fieldWithPath("picture").description("회원가입 진행 시 구글 프로필 이미지 기본값 [Read-Only]")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("Google 이메일에 해당하는 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() throws Exception {
            // given
            LoginResponse response = JIWON.toLoginResponse();
            given(oAuthService.login(AUTHORIZATION_CODE, REDIRECT_URL)).willReturn(response);

            // when
            final OAuthLoginRequest request
                    = new OAuthLoginRequest(AUTHORIZATION_CODE, REDIRECT_URL);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.member.id").exists(),
                            jsonPath("$.member.id").value(1L),
                            jsonPath("$.member.nickname").exists(),
                            jsonPath("$.member.nickname").value(JIWON.getNickname()),
                            jsonPath("$.member.email").exists(),
                            jsonPath("$.member.email").value(JIWON.getEmail()),
                            jsonPath("$.accessToken").exists(),
                            jsonPath("$.accessToken").value(response.accessToken()),
                            jsonPath("$.refreshToken").exists(),
                            jsonPath("$.refreshToken").value(response.refreshToken())
                    )
                    .andDo(
                            document(
                                    "OAuthApi/Login/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("authorizationCode").description("Authorization Code"),
                                            fieldWithPath("redirectUrl").description("redirectUrl")
                                                    .attributes(constraint("Authorization Code 요청 시 redirectUrl과 반드시 동일한 값"))
                                    ),
                                    responseFields(
                                            fieldWithPath("member.id").description("사용자 ID(PK)"),
                                            fieldWithPath("member.nickname").description("사용자 닉네임"),
                                            fieldWithPath("member.email").description("사용자 이메일"),
                                            fieldWithPath("accessToken").description("발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("발급된 Refresh Token (Expire - 2주)")
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
        @DisplayName("로그아웃에 성공한다")
        void success() throws Exception {
            // given
            mockingToken(true, 1L);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
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

    private String generateAuthorizationCodeRequestUri(String redirectUrl) {
        return properties.getAuthUrl() + "?"
                + "response_type=code&"
                + "client_id=" + properties.getClientId() + "&"
                + "scope=" + String.join(" ", properties.getScope()) + "&"
                + "redirect_uri=" + redirectUrl;
    }
}
