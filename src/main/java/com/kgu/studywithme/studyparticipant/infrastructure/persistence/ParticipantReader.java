package com.kgu.studywithme.studyparticipant.infrastructure.persistence;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantReadAdapter;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.kgu.studywithme.member.domain.QMember.member;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipantReader implements ParticipantReadAdapter {
    private final JPAQueryFactory query;

    @Override
    public Member getApplier(final Long studyId, final Long memberId) {
        return Optional.ofNullable(
                query
                        .selectFrom(member)
                        .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                        .where(
                                memberIdEq(memberId),
                                studyIdEq(studyId),
                                participantStatusEq(APPLY)
                        )
                        .fetchOne()
        ).orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND));
    }

    @Override
    public Member getParticipant(final Long studyId, final Long memberId) {
        return Optional.ofNullable(
                query
                        .selectFrom(member)
                        .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                        .where(
                                memberIdEq(memberId),
                                studyIdEq(studyId),
                                participantStatusEq(APPROVE)
                        )
                        .fetchOne()
        ).orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND));
    }

    private BooleanExpression memberIdEq(final Long memberId) {
        return studyParticipant.memberId.eq(memberId);
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyParticipant.studyId.eq(studyId);
    }

    private BooleanExpression participantStatusEq(final ParticipantStatus status) {
        return studyParticipant.status.eq(status);
    }
}