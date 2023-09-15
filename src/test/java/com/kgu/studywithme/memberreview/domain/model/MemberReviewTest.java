package com.kgu.studywithme.memberreview.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MemberReview -> 도메인 [MemberReview] 테스트")
class MemberReviewTest extends ParallelTest {
    @Nested
    @DisplayName("사용자 리뷰 수정")
    class UpdateReview {
        private MemberReview review;

        @BeforeEach
        void setUp() {
            review = MemberReview.doReview(1L, 2L, "Good!!");
        }

        @Test
        @DisplayName("이전과 동일한 내용으로 리뷰를 수정할 수 없다")
        void throwExceptionByReviewSameAsBefore() {
            assertThatThrownBy(() -> review.updateReview(review.getContent()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberReviewErrorCode.REVIEW_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("")
        void success() {
            // when
            final String update = review + " -> Bad...";
            review.updateReview(update);

            // then
            assertThat(review.getContent()).isEqualTo(update);
        }
    }
}
