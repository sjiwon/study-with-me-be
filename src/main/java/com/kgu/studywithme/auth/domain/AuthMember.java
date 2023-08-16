package com.kgu.studywithme.auth.domain;

import com.kgu.studywithme.member.domain.Member;

public record AuthMember(
        MemberInfo member,
        AuthToken token
) {
    public record MemberInfo(
            Long id,
            String nickname,
            String email
    ) {
        public MemberInfo(final Member member) {
            this(
                    member.getId(),
                    member.getNicknameValue(),
                    member.getEmailValue()
            );
        }
    }
}
