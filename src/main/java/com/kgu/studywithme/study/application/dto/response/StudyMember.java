package com.kgu.studywithme.study.application.dto.response;

import com.kgu.studywithme.member.domain.Member;

public record StudyMember(
        Long id,
        String nickname
) {
    public StudyMember(final Member member) {
        this(
                member.getId(),
                member.getNicknameValue()
        );
    }
}
