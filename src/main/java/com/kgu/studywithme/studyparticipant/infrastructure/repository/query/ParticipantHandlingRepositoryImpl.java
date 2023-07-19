package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipantHandlingRepositoryImpl implements ParticipantHandlingRepository {
    private final JPAQueryFactory query;

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

    @Override
    public Optional<StudyParticipant> findApplier(final Long studyId, final Long memberId) {
        return Optional.ofNullable(
                query
                        .selectFrom(studyParticipant)
                        .where(
                                studyIdEq(studyId),
                                memberIdEq(memberId),
                                participantStatusEq(APPLY)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<StudyParticipant> findParticipant(final Long studyId, final Long memberId) {
        return Optional.ofNullable(
                query
                        .selectFrom(studyParticipant)
                        .where(
                                studyIdEq(studyId),
                                memberIdEq(memberId),
                                participantStatusEq(APPROVE)
                        )
                        .fetchOne()
        );
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
