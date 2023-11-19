package com.kgu.studywithme.studyreview.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyReview -> 도메인 [StudyReview] 테스트")
class StudyReviewTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Member member = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toStudy(host).apply(1L);

    @Test
    @DisplayName("스터디 리뷰를 수정한다")
    void updateReview() {
        // given
        final StudyReview review = StudyReview.writeReview(study, member, "좋은 스터디");

        // when
        review.updateReview("별로에요");

        // then
        assertThat(review.getContent()).isEqualTo("별로에요");
    }

    @Test
    @DisplayName("스터디 리뷰 작성자인지 확인한다")
    void isWriter() {
        // given
        final StudyReview review = StudyReview.writeReview(study, member, "좋은 스터디");

        // when
        final boolean actual1 = review.isWriter(host);
        final boolean actual2 = review.isWriter(member);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
