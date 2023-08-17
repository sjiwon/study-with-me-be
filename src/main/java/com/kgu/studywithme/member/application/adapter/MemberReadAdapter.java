package com.kgu.studywithme.member.application.adapter;

import com.kgu.studywithme.member.domain.Member;

public interface MemberReadAdapter {
    Member getById(final Long id);

    Member getByEmail(final String email);
}
