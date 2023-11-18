package com.kgu.studywithme.memberreview.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberReview -> MemberReviewRepository 테스트")
public class MemberReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberReviewRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private Member reviewee;
    private final Member[] reviewers = new Member[3];

    @BeforeEach
    void setUp() {
        reviewee = memberRepository.save(JIWON.toMember());
        reviewers[0] = memberRepository.save(DUMMY1.toMember());
        reviewers[1] = memberRepository.save(DUMMY2.toMember());
        reviewers[2] = memberRepository.save(DUMMY3.toMember());
    }

    @Test
    @DisplayName("리뷰 대상자 ID와 리뷰 작성자 ID로 작성된 리뷰를 조회한다")
    void findByReviewerIdAndRevieweeId() {
        doReview(reviewers[0], reviewers[1], reviewers[2]);

        for (Member reviewer : reviewers) {
            final MemberReview review = sut.getWrittenReview(reviewer.getId(), reviewee.getId());

            assertAll(
                    () -> assertThat(review.getReviewer()).isEqualTo(reviewer),
                    () -> assertThat(review.getReviewee()).isEqualTo(reviewee),
                    () -> assertThat(review.getContent()).isEqualTo("BEST! - " + reviewer.getId())
            );
        }
    }

    @Test
    @DisplayName("Reviewer - Reviewee간 리뷰 레코드가 존재하는지 확인한다")
    void existsByReviewerIdAndRevieweeId() {
        // given
        doReview(reviewers[0], reviewers[2]);

        // when
        final boolean actual1 = sut.existsByReviewerIdAndRevieweeId(reviewers[0].getId(), reviewee.getId());
        final boolean actual2 = sut.existsByReviewerIdAndRevieweeId(reviewers[1].getId(), reviewee.getId());
        final boolean actual3 = sut.existsByReviewerIdAndRevieweeId(reviewers[2].getId(), reviewee.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isTrue()
        );
    }

    private void doReview(final Member... reviewers) {
        final List<MemberReview> memberReviews = new ArrayList<>();
        for (Member reviewer : reviewers) {
            memberReviews.add(MemberReview.doReview(reviewer, reviewee, "BEST! - " + reviewer.getId()));
        }
        sut.saveAll(memberReviews);
    }
}
