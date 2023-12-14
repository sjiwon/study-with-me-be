package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.usecase.GetOAuthLinkUseCase;
import com.kgu.studywithme.auth.application.usecase.LogoutUseCase;
import com.kgu.studywithme.auth.application.usecase.OAuthLoginUseCase;
import com.kgu.studywithme.auth.application.usecase.command.LogoutCommand;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginCommand;
import com.kgu.studywithme.auth.application.usecase.query.GetOAuthLink;
import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;
import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.auth.presentation.dto.response.LoginResponse;
import com.kgu.studywithme.auth.utils.TokenResponseWriter;
import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.global.resolver.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1-1. OAuth 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthApiController {
    private final GetOAuthLinkUseCase getOAuthLinkUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final TokenResponseWriter tokenResponseWriter;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "Provider별 OAuth 인증을 위한 URL을 받는 EndPoint")
    @GetMapping(value = "/access/{provider}", params = {"redirectUri"})
    public ResponseWrapper<String> queryOAuthLink(
            @PathVariable final String provider,
            @RequestParam final String redirectUri
    ) {
        final String oAuthLink = getOAuthLinkUseCase.invoke(new GetOAuthLink(
                OAuthProvider.from(provider),
                redirectUri
        ));
        return ResponseWrapper.from(oAuthLink);
    }

    @Operation(summary = "Authorization Code를 통해서 Provider별 인증을 위한 EndPoint")
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody @Valid final OAuthLoginRequest request,
            final HttpServletResponse response
    ) {
        final AuthMember authMember = oAuthLoginUseCase.invoke(new OAuthLoginCommand(
                OAuthProvider.from(provider),
                request.authorizationCode(),
                request.redirectUri(),
                request.state()
        ));
        tokenResponseWriter.applyAccessToken(response, authMember.token().accessToken());
        tokenResponseWriter.applyRefreshToken(response, authMember.token().refreshToken());

        return ResponseEntity.ok(new LoginResponse(
                authMember.member().id(),
                authMember.member().nickname(),
                authMember.member().email()
        ));
    }

    @Operation(summary = "로그아웃 EndPoint")
    @CheckAuthUser
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Auth final Authenticated authenticated) {
        logoutUseCase.invoke(new LogoutCommand(authenticated.id()));
        return ResponseEntity.noContent().build();
    }
}
