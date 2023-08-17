package com.kgu.studywithme.study.infrastructure.query;

public interface StudyVerificationRepository {
    boolean isHost(final Long studyId, final Long memberId);
}
