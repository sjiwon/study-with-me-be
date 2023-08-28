package com.kgu.studywithme.studyparticipant.application.adapter;

import com.kgu.studywithme.member.domain.Member;

public interface ParticipateMemberReadAdapter {
    Member getApplier(final Long studyId, final Long memberId);

    Member getParticipant(final Long studyId, final Long memberId);
}
