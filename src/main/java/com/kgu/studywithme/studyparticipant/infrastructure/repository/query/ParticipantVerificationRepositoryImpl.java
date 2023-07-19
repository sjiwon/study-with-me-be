package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.*;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipantVerificationRepositoryImpl implements ParticipantVerificationRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isParticipant(final Long studyId, final Long memberId) {
        return query
                .select(participant.id)
                .from(participant)
                .where(
                        studyIdEq(studyId),
                        participantIdEq(memberId),
                        participant.status.eq(APPROVE)
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isApplierOrParticipant(final Long studyId, final Long memberId) {
        return query
                .select(participant.id)
                .from(participant)
                .where(
                        studyIdEq(studyId),
                        participantIdEq(memberId),
                        participant.status.in(APPLY, APPROVE)
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isGraduatedParticipant(final Long studyId, final Long memberId) {
        return query
                .select(participant.id)
                .from(participant)
                .where(
                        studyIdEq(studyId),
                        participantIdEq(memberId),
                        participant.status.eq(GRADUATED)
                )
                .fetchOne() != null;
    }

    @Override
    public boolean isAlreadyCancelOrGraduatedParticipant(final Long studyId, final Long memberId) {
        return query
                .select(participant.id)
                .from(participant)
                .where(
                        studyIdEq(studyId),
                        participantIdEq(memberId),
                        participant.status.in(CALCEL, GRADUATED)
                )
                .fetchOne() != null;
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return participant.study.id.eq(studyId);
    }

    private BooleanExpression participantIdEq(final Long memberId) {
        return participant.member.id.eq(memberId);
    }
}
