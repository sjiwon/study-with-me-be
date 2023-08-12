package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.LEAVE;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipantVerificationRepositoryImpl implements ParticipantVerificationRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isApplier(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.id)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId),
                        participantStatusEq(APPLY)
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isParticipant(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.id)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId),
                        participantStatusEq(APPROVE)
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isApplierOrParticipant(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.id)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId),
                        participantStatusIn(Set.of(APPLY, APPROVE))
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isGraduatedParticipant(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.id)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId),
                        participantStatusEq(GRADUATED)
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isAlreadyLeaveOrGraduatedParticipant(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.id)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId),
                        participantStatusIn(Set.of(LEAVE, GRADUATED))
                )
                .fetchOne() != null;
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

    private BooleanExpression participantStatusIn(final Set<ParticipantStatus> status) {
        return studyParticipant.status.in(status);
    }
}
