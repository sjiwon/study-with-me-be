package com.kgu.studywithme.studyparticipant.domain.repository.query;

import com.kgu.studywithme.member.domain.model.Member;

public interface ParticipateMemberReader {
    Member getApplier(final Long studyId, final Long memberId);

    Member getParticipant(final Long studyId, final Long memberId);
}
