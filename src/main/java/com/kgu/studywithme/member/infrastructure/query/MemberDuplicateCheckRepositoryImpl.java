package com.kgu.studywithme.member.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.member.domain.QMember.member;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberDuplicateCheckRepositoryImpl implements MemberDuplicateCheckRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isEmailExists(final String email) {
        return query
                .select(member.id)
                .from(member)
                .where(member.email.value.eq(email))
                .fetchFirst() != null;
    }

    @Override
    public boolean isNicknameExists(final String nickname) {
        return query
                .select(member.id)
                .from(member)
                .where(member.nickname.value.eq(nickname))
                .fetchFirst() != null;
    }

    @Override
    public boolean isNicknameUsedByOther(final Long memberId, final String nickname) {
        return query
                .select(member.id)
                .from(member)
                .where(
                        member.id.ne(memberId),
                        member.nickname.value.eq(nickname)
                )
                .fetchFirst() != null;
    }

    @Override
    public boolean isPhoneExists(final String phone) {
        return query
                .select(member.id)
                .from(member)
                .where(member.phone.eq(phone))
                .fetchFirst() != null;
    }

    @Override
    public boolean isPhoneUsedByOther(final Long memberId, final String phone) {
        return query
                .select(member.id)
                .from(member)
                .where(
                        member.id.ne(memberId),
                        member.phone.eq(phone)
                )
                .fetchFirst() != null;
    }
}
