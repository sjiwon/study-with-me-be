package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;

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
                        participant.study.id.eq(studyId),
                        participant.member.id.eq(memberId),
                        participant.status.eq(APPROVE)
                )
                .fetchOne() != null;
    }
}
