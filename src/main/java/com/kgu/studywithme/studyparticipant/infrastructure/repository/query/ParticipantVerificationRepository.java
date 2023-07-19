package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

public interface ParticipantVerificationRepository {
    boolean isParticipant(final Long studyId, final Long memberId);

    boolean isApplierOrParticipant(final Long studyId, final Long memberId);

    boolean isGraduatedParticipant(final Long studyId, final Long memberId);

    boolean isAlreadyCancelOrGraduatedParticipant(final Long studyId, final Long memberId);
}
