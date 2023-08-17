package com.kgu.studywithme.studyparticipant.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

import static com.kgu.studywithme.member.domain.QMember.member;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipantHandlingRepositoryImpl implements ParticipantHandlingRepository {
    private final JPAQueryFactory query;

    @Override
    public Optional<Member> findApplier(final Long studyId, final Long memberId) {
        return Optional.ofNullable(
                query
                        .selectFrom(member)
                        .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                        .where(
                                member.id.eq(memberId),
                                studyIdEq(studyId),
                                participantStatusEq(APPLY)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Member> findParticipant(final Long studyId, final Long memberId) {
        return Optional.ofNullable(
                query
                        .selectFrom(member)
                        .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                        .where(
                                member.id.eq(memberId),
                                studyIdEq(studyId),
                                participantStatusEq(APPROVE)
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<Long> findStudyParticipantIds(final Long studyId) {
        return query
                .select(studyParticipant.memberId)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        participantStatusEq(APPROVE)
                )
                .fetch();
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Override
    public long deleteApplier(final Long studyId, final Long memberId) {
        return query
                .delete(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId),
                        participantStatusEq(APPLY)
                )
                .execute();
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Override
    public void updateParticipantStatus(final Long studyId, final Long memberId, final ParticipantStatus status) {
        query
                .update(studyParticipant)
                .set(studyParticipant.status, status)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .execute();
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyParticipant.studyId.eq(studyId);
    }

    private BooleanExpression memberIdEq(final Long memberId) {
        return studyParticipant.memberId.eq(memberId);
    }

    private BooleanExpression participantStatusEq(final ParticipantStatus status) {
        return studyParticipant.status.eq(status);
    }
}
