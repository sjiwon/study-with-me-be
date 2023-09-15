package com.kgu.studywithme.auth.domain.model;

import com.kgu.studywithme.member.domain.model.Member;

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
                    member.getNickname().getValue(),
                    member.getEmail().getValue()
            );
        }
    }
}
