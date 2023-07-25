package com.kgu.studywithme.memberreview.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberReview -> MemberReviewRepository 테스트")
public class MemberReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberReviewRepository memberReviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member reviewee;
    private final Member[] reviewers = new Member[5];

    @BeforeEach
    void setUp() {
        reviewee = memberRepository.save(JIWON.toMember());
        reviewers[0] = memberRepository.save(DUMMY1.toMember());
        reviewers[1] = memberRepository.save(DUMMY2.toMember());
        reviewers[2] = memberRepository.save(DUMMY3.toMember());
        reviewers[3] = memberRepository.save(DUMMY4.toMember());
        reviewers[4] = memberRepository.save(DUMMY5.toMember());
    }

    @Test
    @DisplayName("사용자가 받은 리뷰를 조회한다")
    void findReviewContentById() {
        /* 3명 피어리뷰 */
        doReview(reviewers[0], reviewers[1], reviewers[2]);

        final List<String> result1 = memberReviewRepository.findReviewContentById(reviewee.getId());
        assertAll(
                () -> assertThat(result1).hasSize(3),
                () -> assertThat(result1)
                        .containsExactly(
                                "BEST! - " + reviewers[0].getId(),
                                "BEST! - " + reviewers[1].getId(),
                                "BEST! - " + reviewers[2].getId()
                        )
        );

        /* 추가 2명 피어리뷰 */
        doReview(reviewers[3], reviewers[4]);

        final List<String> result2 = memberReviewRepository.findReviewContentById(reviewee.getId());
        assertAll(
                () -> assertThat(result2).hasSize(5),
                () -> assertThat(result2)
                        .containsExactly(
                                "BEST! - " + reviewers[0].getId(),
                                "BEST! - " + reviewers[1].getId(),
                                "BEST! - " + reviewers[2].getId(),
                                "BEST! - " + reviewers[3].getId(),
                                "BEST! - " + reviewers[4].getId()
                        )
        );
    }

    @Test
    @DisplayName("리뷰 대상자 ID와 리뷰 작성자 ID로 리뷰를 조회한다")
    void findByReviewerIdAndRevieweeId() {
        doReview(reviewers[0], reviewers[1], reviewers[2], reviewers[3], reviewers[4]);

        for (Member reviewer : reviewers) {
            final MemberReview review =
                    memberReviewRepository.findByReviewerIdAndRevieweeId(reviewer.getId(), reviewee.getId())
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
        final boolean actual1 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[0].getId(), reviewee.getId());
        final boolean actual2 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[1].getId(), reviewee.getId());
        final boolean actual3 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[2].getId(), reviewee.getId());
        final boolean actual4 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[3].getId(), reviewee.getId());
        final boolean actual5 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[4].getId(), reviewee.getId());

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
        memberReviewRepository.saveAll(
                Arrays.stream(reviewers)
                        .map(reviewer ->
                                MemberReview.doReview(
                                        reviewer.getId(),
                                        reviewee.getId(),
                                        "BEST! - " + reviewer.getId()
                                )
                        )
                        .toList()
        );
    }
}
