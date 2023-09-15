package com.kgu.studywithme.auth.presentation;

import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenUseCase;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.auth.utils.ExtractToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1-2. 토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final ReissueTokenUseCase reissueTokenUseCase;

    @Operation(summary = "RefreshToken을 통한 토큰 재발급 EndPoint")
    @PostMapping
    public AuthToken reissueToken(
            @ExtractPayload final Long memberId,
            @ExtractToken final String refreshToken
    ) {
        return reissueTokenUseCase.invoke(new ReissueTokenUseCase.Command(memberId, refreshToken));
    }
}
