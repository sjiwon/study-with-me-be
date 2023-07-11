package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.OAuthService;
import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.application.usecase.query.QueryOAuthLinkUseCase;
import com.kgu.studywithme.auth.presentation.dto.request.OAuthLoginRequest;
import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1-1. 로그인(OAuth) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthApiController {
    private final QueryOAuthLinkUseCase queryOAuthLinkUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final OAuthService oAuthService;

    @Operation(summary = "Provider별 OAuth 인증을 위한 URL을 받는 EndPoint")
    @GetMapping(value = "/access/{provider}", params = {"redirectUrl"})
    public ResponseEntity<SimpleResponseWrapper<String>> access(
            @PathVariable final String provider,
            @RequestParam final String redirectUrl
    ) {
        final String oAuthLink = queryOAuthLinkUseCase.createOAuthLink(
                new QueryOAuthLinkUseCase.Query(
                        OAuthProvider.of(provider),
                        redirectUrl
                )
        );
        return ResponseEntity.ok(new SimpleResponseWrapper<>(oAuthLink));
    }

    @Operation(summary = "Authorization Code를 통해서 Provider별 인증을 위한 EndPoint")
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody @Valid final OAuthLoginRequest request
    ) {
        final LoginResponse response = oAuthLoginUseCase.login(
                new OAuthLoginUseCase.Command(
                        OAuthProvider.of(provider),
                        request.authorizationCode(),
                        request.redirectUrl()
                )
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃 EndPoint")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@ExtractPayload final Long memberId) {
        oAuthService.logout(memberId);
        return ResponseEntity.noContent().build();
    }
}
