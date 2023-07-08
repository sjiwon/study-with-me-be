package com.kgu.studywithme.auth.controller;

import com.kgu.studywithme.auth.service.TokenReissueService;
import com.kgu.studywithme.auth.service.dto.response.TokenResponse;
import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.auth.utils.ExtractToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1-2. 토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final TokenReissueService tokenReissueService;

    @Operation(summary = "RefreshToken을 통한 토큰 재발급 EndPoint")
    @PostMapping
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractPayload Long memberId, @ExtractToken String refreshToken) {
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(memberId, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
