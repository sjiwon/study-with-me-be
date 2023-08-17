package com.kgu.studywithme.studyparticipant.infrastructure.query;

public interface ParticipantVerificationRepository {
    boolean isApplier(final Long studyId, final Long memberId);

    boolean isParticipant(final Long studyId, final Long memberId);

    boolean isApplierOrParticipant(final Long studyId, final Long memberId);

    boolean isGraduatedParticipant(final Long studyId, final Long memberId);

    boolean isAlreadyLeaveOrGraduatedParticipant(final Long studyId, final Long memberId);
}
