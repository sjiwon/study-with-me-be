package com.kgu.studywithme.study.infrastructure.repository.query;

public interface ParticipantVerificationRepository {
    boolean isParticipant(final Long studyId, final Long memberId);
    boolean isGraduatedParticipant(final Long studyId, final Long memberId);
}
