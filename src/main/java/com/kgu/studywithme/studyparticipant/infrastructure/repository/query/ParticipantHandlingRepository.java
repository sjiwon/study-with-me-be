package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipantHandlingRepository {
    Optional<Member> findApplier(final Long studyId, final Long memberId);

    Optional<Member> findParticipant(final Long studyId, final Long memberId);

    List<Long> findStudyParticipantIds(final Long studyId);

    long deleteApplier(final Long studyId, final Long memberId);

    void updateParticipantStatus(final Long studyId, final Long memberId, final ParticipantStatus status);
}
