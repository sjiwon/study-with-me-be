package com.kgu.studywithme.auth.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class OAuthService {
    private final TokenManager tokenManager;

    @StudyWithMeWritableTransactional
    public void logout(final Long memberId) {
        tokenManager.deleteRefreshTokenByMemberId(memberId);
    }
}
