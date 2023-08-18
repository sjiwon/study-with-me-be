package com.kgu.studywithme.study.application.adapter;

public interface StudyVerificationRepositoryAdapter {
    boolean isHost(final Long studyId, final Long memberId);
}
