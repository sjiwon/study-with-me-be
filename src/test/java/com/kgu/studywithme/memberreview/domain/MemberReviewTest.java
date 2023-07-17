package com.kgu.studywithme.memberreview.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberReview -> 도메인 [MemberReview] 테스트")
class MemberReviewTest {
    @Test
    @DisplayName("사용자 리뷰(MemberReview)를 수정한다")
    void updateReview() {
        // given
        final MemberReview memberReview = MemberReview.doReview(1L, 2L, "Good!!");

        // when
        final String update = "Bad..";
        memberReview.updateReview(update);

        // then
        assertThat(memberReview.getContent()).isEqualTo(update);
    }

    @Test
    @DisplayName("동일한 리뷰 내용인지 확인한다")
    void isReviewSame() {
        // given
        final MemberReview memberReview = MemberReview.doReview(1L, 2L, "Good!!");

        // when
        boolean actual1 = memberReview.isReviewSame("Good!!");
        boolean actual2 = memberReview.isReviewSame("Bad..");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
