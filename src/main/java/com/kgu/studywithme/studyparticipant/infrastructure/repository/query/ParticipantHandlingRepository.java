package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;

import java.util.Optional;

public interface ParticipantHandlingRepository {
    long deleteApplier(final Long studyId, final Long memberId);

    Optional<StudyParticipant> findApplier(final Long studyId, final Long memberId);

    Optional<StudyParticipant> findParticipant(final Long studyId, final Long memberId);
}
