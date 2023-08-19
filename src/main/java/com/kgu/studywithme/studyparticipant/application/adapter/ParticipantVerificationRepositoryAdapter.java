package com.kgu.studywithme.studyparticipant.application.adapter;

public interface ParticipantVerificationRepositoryAdapter {
    boolean isParticipant(final Long studyId, final Long memberId);

    boolean isApplierOrParticipant(final Long studyId, final Long memberId);

    boolean isGraduatedParticipant(final Long studyId, final Long memberId);

    boolean isAlreadyLeaveOrGraduatedParticipant(final Long studyId, final Long memberId);
}
