package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.usecase.ReissueTokenUseCase;
import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenCommand;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.utils.TokenResponseWriter;
import com.kgu.studywithme.global.annotation.ExtractToken;
import com.kgu.studywithme.global.annotation.TokenType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final TokenResponseWriter tokenResponseWriter;

    @Operation(summary = "RefreshToken을 통한 토큰 재발급 EndPoint")
    @PostMapping
    public ResponseEntity<Void> reissueToken(
            @ExtractToken(tokenType = TokenType.REFRESH) final String refreshToken,
            final HttpServletResponse response
    ) {
        final AuthToken authToken = reissueTokenUseCase.invoke(new ReissueTokenCommand(refreshToken));
        tokenResponseWriter.applyToken(response, authToken);

        return ResponseEntity.noContent().build();
    }
}
