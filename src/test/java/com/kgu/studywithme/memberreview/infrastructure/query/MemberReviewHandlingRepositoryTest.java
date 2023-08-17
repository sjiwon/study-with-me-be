package com.kgu.studywithme.memberreview.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.memberreview.application.adapter.MemberReviewHandlingRepositoryAdapter;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.infrastructure.persistence.MemberReviewJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MemberReviewHandlingRepository.class)
public class MemberReviewHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberReviewHandlingRepositoryAdapter memberReviewHandlingRepositoryAdapter;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberReviewJpaRepository memberReviewJpaRepository;

    private Member reviewee;
    private final Member[] reviewers = new Member[5];

    @BeforeEach
    void setUp() {
        reviewee = memberJpaRepository.save(JIWON.toMember());
        reviewers[0] = memberJpaRepository.save(DUMMY1.toMember());
        reviewers[1] = memberJpaRepository.save(DUMMY2.toMember());
        reviewers[2] = memberJpaRepository.save(DUMMY3.toMember());
        reviewers[3] = memberJpaRepository.save(DUMMY4.toMember());
        reviewers[4] = memberJpaRepository.save(DUMMY5.toMember());
    }

    @Test
    @DisplayName("리뷰 대상자 ID와 리뷰 작성자 ID로 리뷰를 조회한다")
    void findByReviewerIdAndRevieweeId() {
        doReview(reviewers[0], reviewers[1], reviewers[2], reviewers[3], reviewers[4]);

        for (Member reviewer : reviewers) {
            final MemberReview review =
                    memberReviewHandlingRepositoryAdapter.getWrittenReviewForReviewee(reviewer.getId(), reviewee.getId())
                            .orElseThrow();

            assertAll(
                    () -> assertThat(review.getReviewerId()).isEqualTo(reviewer.getId()),
                    () -> assertThat(review.getRevieweeId()).isEqualTo(reviewee.getId()),
                    () -> assertThat(review.getContent()).isEqualTo("BEST! - " + reviewer.getId())
            );
        }
    }

    @Test
    @DisplayName("Reviewer - Reviewee간 리뷰 레코드가 존재하는지 확인한다")
    void existsByReviewerIdAndRevieweeId() {
        // given
        doReview(reviewers[1], reviewers[3]);

        // when
        final boolean actual1 = memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(reviewers[0].getId(), reviewee.getId());
        final boolean actual2 = memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(reviewers[1].getId(), reviewee.getId());
        final boolean actual3 = memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(reviewers[2].getId(), reviewee.getId());
        final boolean actual4 = memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(reviewers[3].getId(), reviewee.getId());
        final boolean actual5 = memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(reviewers[4].getId(), reviewee.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isTrue(),
                () -> assertThat(actual5).isFalse()
        );
    }

    private void doReview(final Member... reviewers) {
        final List<MemberReview> memberReviews = new ArrayList<>();
        for (Member reviewer : reviewers) {
            memberReviews.add(
                    MemberReview.doReview(
                            reviewer.getId(),
                            reviewee.getId(),
                            "BEST! - " + reviewer.getId()
                    )
            );
        }
        memberReviewJpaRepository.saveAll(memberReviews);
    }
}
