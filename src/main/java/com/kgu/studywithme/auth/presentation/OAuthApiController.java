package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.OAuthService;
import com.kgu.studywithme.auth.application.dto.response.LoginResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1-1. 로그인(OAuth) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthApiController {
    private final OAuthUri oAuthUri;
    private final OAuthService oAuthService;

    @Operation(summary = "Google OAuth 인증을 위한 URL을 받는 EndPoint")
    @GetMapping(value = "/access", params = {"redirectUrl"})
    public ResponseEntity<SimpleResponseWrapper<String>> access(@RequestParam final String redirectUrl) {
        final String link = oAuthUri.generate(redirectUrl);
        return ResponseEntity.ok(new SimpleResponseWrapper<>(link));
    }

    @Operation(summary = "Authorization Code를 통해서 Google OAuth Server에 인증을 위한 EndPoint")
    @GetMapping(value = "/login", params = {"authorizationCode", "redirectUrl"})
    public ResponseEntity<LoginResponse> login(
            @RequestParam final String authorizationCode,
            @RequestParam final String redirectUrl
    ) {
        final LoginResponse response = oAuthService.login(authorizationCode, redirectUrl);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃 EndPoint")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@ExtractPayload final Long memberId) {
        oAuthService.logout(memberId);
        return ResponseEntity.noContent().build();
    }
}