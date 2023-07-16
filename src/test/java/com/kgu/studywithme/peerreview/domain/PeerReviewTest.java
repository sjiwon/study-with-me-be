package com.kgu.studywithme.peerreview.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PeerReview -> 도메인 [PeerReview] 테스트")
class PeerReviewTest {
    @Test
    @DisplayName("사용자 리뷰(PeerReview)를 수정한다")
    void updateReview() {
        // given
        final PeerReview peerReview = PeerReview.doReview(1L, 2L, "Good!!");

        // when
        final String update = "Bad..";
        peerReview.updateReview(update);

        // then
        assertThat(peerReview.getContent()).isEqualTo(update);
    }
}
