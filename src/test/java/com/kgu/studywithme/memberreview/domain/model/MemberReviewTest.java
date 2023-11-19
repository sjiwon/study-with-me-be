package com.kgu.studywithme.memberreview.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MemberReview -> 도메인 [MemberReview] 테스트")
class MemberReviewTest extends ParallelTest {
    private final Member reviewer = JIWON.toMember().apply(1L);
    private final Member reviewee = GHOST.toMember().apply(2L);

    @Nested
    @DisplayName("사용자 리뷰 수정")
    class UpdateReview {
        @Test
        @DisplayName("이전과 동일한 내용으로 리뷰를 수정할 수 없다")
        void throwExceptionByReviewSameAsBefore() {
            // given
            final MemberReview review = MemberReview.doReview(reviewer, reviewee, "Good!!");

            // when - then
            assertThatThrownBy(() -> review.updateReview(review.getContent()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberReviewErrorCode.REVIEW_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("사용자에 대한 리뷰를 작성한다")
        void success() {
            // given
            final MemberReview review = MemberReview.doReview(reviewer, reviewee, "Good!!");

            // when
            final String update = review + "_Bad...";
            review.updateReview(update);

            // then
            assertThat(review.getContent()).isEqualTo(update);
        }
    }
}
