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

    private final Member[] reviewers = new Member[5];
    private Member reviewee;

    @BeforeEach
    void setUp() {
        reviewers[0] = memberRepository.save(DUMMY1.toMember());
        reviewers[1] = memberRepository.save(DUMMY2.toMember());
        reviewers[2] = memberRepository.save(DUMMY3.toMember());
        reviewers[3] = memberRepository.save(DUMMY4.toMember());
        reviewers[4] = memberRepository.save(DUMMY5.toMember());
        reviewee = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("사용자가 받은 리뷰를 조회한다")
    void findAllReviewContentByRevieweeId() {
        /* 3명 피어리뷰 */
        doReview(reviewers[0], reviewers[1], reviewers[2]);

        List<String> result1 = memberReviewRepository.findAllReviewContentByRevieweeId(reviewee.getId());
        assertThat(result1).hasSize(3);
        assertThat(result1).containsExactly(
                "BEST! - " + reviewers[0].getId(),
                "BEST! - " + reviewers[1].getId(),
                "BEST! - " + reviewers[2].getId()
        );

        /* 추가 2명 피어리뷰 */
        doReview(reviewers[3], reviewers[4]);

        List<String> result2 = memberReviewRepository.findAllReviewContentByRevieweeId(reviewee.getId());
        assertThat(result2).hasSize(5);
        assertThat(result2).containsExactly(
                "BEST! - " + reviewers[0].getId(),
                "BEST! - " + reviewers[1].getId(),
                "BEST! - " + reviewers[2].getId(),
                "BEST! - " + reviewers[3].getId(),
                "BEST! - " + reviewers[4].getId()
        );
    }

    @Test
    @DisplayName("리뷰 대상자 ID와 리뷰 작성자 ID로 리뷰를 조회한다")
    void findByReviewerIdAndRevieweeId() {
        doReview(reviewers[0], reviewers[1], reviewers[2], reviewers[3], reviewers[4]);
        assertThatMemberReviewMatch();
    }

    @Test
    @DisplayName("Reviewer - Reviewee간 리뷰 레코드가 존재하는지 확인한다")
    void existsByReviewerIdAndRevieweeId() {
        // given
        doReview(reviewers[1], reviewers[3]);

        // when
        boolean actual1 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[0].getId(), reviewee.getId());
        boolean actual2 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[1].getId(), reviewee.getId());
        boolean actual3 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[2].getId(), reviewee.getId());
        boolean actual4 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[3].getId(), reviewee.getId());
        boolean actual5 = memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewers[4].getId(), reviewee.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isTrue(),
                () -> assertThat(actual5).isFalse()
        );
    }

    private void doReview(Member... reviewers) {
        Arrays.stream(reviewers)
                .forEach(reviewer ->
                        memberReviewRepository.save(
                                MemberReview.doReview(
                                        reviewer.getId(),
                                        reviewee.getId(),
                                        "BEST! - " + reviewer.getId()
                                )
                        )
                );
    }

    private void assertThatMemberReviewMatch() {
        for (Member reviewer : reviewers) {
            MemberReview memberReview = getMemberReview(reviewer.getId(), reviewee.getId());

            assertAll(
                    () -> assertThat(memberReview.getReviewerId()).isEqualTo(reviewer.getId()),
                    () -> assertThat(memberReview.getRevieweeId()).isEqualTo(reviewee.getId()),
                    () -> assertThat(memberReview.getContent()).isEqualTo("BEST! - " + reviewer.getId())
            );
        }
    }

    private MemberReview getMemberReview(Long reviewerId, Long revieweeId) {
        return memberReviewRepository.findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow();
    }
}
