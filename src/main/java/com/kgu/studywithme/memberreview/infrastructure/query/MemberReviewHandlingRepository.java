package com.kgu.studywithme.memberreview.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.memberreview.application.adapter.MemberReviewHandlingRepositoryAdapter;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.kgu.studywithme.memberreview.domain.QMemberReview.memberReview;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberReviewHandlingRepository implements MemberReviewHandlingRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public Optional<MemberReview> getWrittenReviewForReviewee(final Long reviewerId, final Long revieweeId) {
        return Optional.ofNullable(
                query
                        .selectFrom(memberReview)
                        .where(
                                reviewerIdEq(reviewerId),
                                revieweeIdEq(revieweeId)
                        )
                        .fetchOne()
        );
    }

    @Override
    public boolean alreadyReviewedForMember(final Long reviewerId, final Long memberId) {
        return query
                .select(memberReview.id)
                .from(memberReview)
                .where(
                        reviewerIdEq(reviewerId),
                        revieweeIdEq(memberId)
                )
                .fetchFirst() != null;
    }

    private BooleanExpression reviewerIdEq(final Long reviewerId) {
        return memberReview.reviewerId.eq(reviewerId);
    }

    private BooleanExpression revieweeIdEq(final Long revieweeId) {
        return memberReview.revieweeId.eq(revieweeId);
    }
}
