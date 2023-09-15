package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.application.usecase.query.QueryOAuthLinkUseCase;
import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider;
import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.dto.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final QueryOAuthLinkUseCase queryOAuthLinkUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "Provider별 OAuth 인증을 위한 URL을 받는 EndPoint")
    @GetMapping(value = "/access/{provider}", params = {"redirectUri"})
    public ResponseWrapper<String> queryOAuthLink(
            @PathVariable final String provider,
            @RequestParam final String redirectUri
    ) {
        final String oAuthLink = queryOAuthLinkUseCase.invoke(
                new QueryOAuthLinkUseCase.Query(
                        OAuthProvider.from(provider),
                        redirectUri
                )
        );
        return ResponseWrapper.from(oAuthLink);
    }

    @Operation(summary = "Authorization Code를 통해서 Provider별 인증을 위한 EndPoint")
    @PostMapping("/login/{provider}")
    public AuthMember login(
            @PathVariable final String provider,
            @RequestBody @Valid final OAuthLoginRequest request
    ) {
        return oAuthLoginUseCase.invoke(
                new OAuthLoginUseCase.Command(
                        OAuthProvider.from(provider),
                        request.authorizationCode(),
                        request.redirectUri(),
                        request.state()
                )
        );
    }

    @Operation(summary = "로그아웃 EndPoint")
    @CheckAuthUser
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@ExtractPayload final Long memberId) {
        logoutUseCase.invoke(new LogoutUseCase.Command(memberId));
        return ResponseEntity.noContent().build();
    }
}
