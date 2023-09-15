package com.kgu.studywithme.member.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepositoryAdapter;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.member.domain.model.QMember.member;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberDuplicateCheckRepository implements MemberDuplicateCheckRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public boolean isEmailExists(final String email) {
        return query
                .select(member.id)
                .from(member)
                .where(emailEq(email))
                .fetchFirst() != null;
    }

    @Override
    public boolean isNicknameExists(final String nickname) {
        return query
                .select(member.id)
                .from(member)
                .where(nicknameEq(nickname))
                .fetchFirst() != null;
    }

    @Override
    public boolean isNicknameUsedByOther(final Long memberId, final String nickname) {
        final Long nicknameUsedMemberId = query
                .select(member.id)
                .from(member)
                .where(nicknameEq(nickname))
                .fetchFirst();

        return nicknameUsedMemberId != null && !nicknameUsedMemberId.equals(memberId);
    }

    @Override
    public boolean isPhoneExists(final String phone) {
        return query
                .select(member.id)
                .from(member)
                .where(phoneNumberEq(phone))
                .fetchFirst() != null;
    }

    @Override
    public boolean isPhoneUsedByOther(final Long memberId, final String phone) {
        final Long phoneUsedMemberId = query
                .select(member.id)
                .from(member)
                .where(phoneNumberEq(phone))
                .fetchFirst();

        return phoneUsedMemberId != null && !phoneUsedMemberId.equals(memberId);
    }

    private BooleanExpression emailEq(final String email) {
        return member.email.value.eq(email);
    }

    private BooleanExpression nicknameEq(final String nickname) {
        return member.nickname.value.eq(nickname);
    }

    private BooleanExpression phoneNumberEq(final String phone) {
        return member.phone.value.eq(phone);
    }
}
