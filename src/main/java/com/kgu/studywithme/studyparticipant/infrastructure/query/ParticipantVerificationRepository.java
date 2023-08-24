package com.kgu.studywithme.studyparticipant.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.LEAVE;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipantVerificationRepository implements ParticipantVerificationRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public boolean isParticipant(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.status)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .fetchOne() == APPROVE;
    }

    @Override
    public boolean isGraduatedParticipant(final Long studyId, final Long memberId) {
        return query
                .select(studyParticipant.status)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .fetchOne() == GRADUATED;
    }

    @Override
    public boolean isApplierOrParticipant(final Long studyId, final Long memberId) {
        final ParticipantStatus participantStatus = query
                .select(studyParticipant.status)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .fetchOne();

        return participantStatus != null && isApplyOrApproveStatus(participantStatus);
    }

    private boolean isApplyOrApproveStatus(final ParticipantStatus participantStatus) {
        return participantStatus == APPLY || participantStatus == APPROVE;
    }

    @Override
    public boolean isAlreadyLeaveOrGraduatedParticipant(final Long studyId, final Long memberId) {
        final ParticipantStatus participantStatus = query
                .select(studyParticipant.status)
                .from(studyParticipant)
                .where(
                        studyIdEq(studyId),
                        memberIdEq(memberId)
                )
                .fetchOne();

        return participantStatus != null && isLeaveOrGraduatedStatus(participantStatus);
    }

    private boolean isLeaveOrGraduatedStatus(final ParticipantStatus participantStatus) {
        return participantStatus == LEAVE || participantStatus == GRADUATED;
    }

    private BooleanExpression studyIdEq(final Long studyId) {
        return studyParticipant.studyId.eq(studyId);
    }

    private BooleanExpression memberIdEq(final Long memberId) {
        return studyParticipant.memberId.eq(memberId);
    }
}
