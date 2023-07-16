package com.kgu.studywithme.peerreview.domain;

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

@DisplayName("PeerReview -> PeerReviewRepository 테스트")
public class PeerReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private PeerReviewRepository peerReviewRepository;

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
    @DisplayName("사용자가 받은 PeerReview를 조회한다")
    void findAllReviewContentByRevieweeId() {
        /* 3명 피어리뷰 */
        doReview(reviewers[0], reviewers[1], reviewers[2]);

        List<String> result1 = peerReviewRepository.findAllReviewContentByRevieweeId(reviewee.getId());
        assertThat(result1).hasSize(3);
        assertThat(result1).containsExactly(
                "BEST! - " + reviewers[0].getId(),
                "BEST! - " + reviewers[1].getId(),
                "BEST! - " + reviewers[2].getId()
        );

        /* 추가 2명 피어리뷰 */
        doReview(reviewers[3], reviewers[4]);

        List<String> result2 = peerReviewRepository.findAllReviewContentByRevieweeId(reviewee.getId());
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
        assertThatPeerReviewMatch();
    }

    private void doReview(Member... reviewers) {
        Arrays.stream(reviewers)
                .forEach(reviewer ->
                        peerReviewRepository.save(
                                PeerReview.doReview(
                                        reviewer.getId(),
                                        reviewee.getId(),
                                        "BEST! - " + reviewer.getId()
                                )
                        )
                );
    }

    private void assertThatPeerReviewMatch() {
        for (Member reviewer : reviewers) {
            PeerReview peerReview = getPeerReview(reviewer.getId(), reviewee.getId());

            assertAll(
                    () -> assertThat(peerReview.getReviewerId()).isEqualTo(reviewer.getId()),
                    () -> assertThat(peerReview.getRevieweeId()).isEqualTo(reviewee.getId()),
                    () -> assertThat(peerReview.getContent()).isEqualTo("BEST! - " + reviewer.getId())
            );
        }
    }

    private PeerReview getPeerReview(Long reviewerId, Long revieweeId) {
        return peerReviewRepository.findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow();
    }
}
