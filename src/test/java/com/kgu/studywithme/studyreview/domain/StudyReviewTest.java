package com.kgu.studywithme.studyreview.domain;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyReview -> 도메인 [StudyReview] 테스트")
class StudyReviewTest {
    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(memberA.getId());
    private final StudyReview review = StudyReview.writeReview(
            study.getId(),
            memberB.getId(),
            "좋은 스터디"
    );

    @Test
    @DisplayName("스터디 리뷰를 수정한다")
    void updateReview() {
        // when
        review.updateReview("안좋은 스터디");

        // then
        assertThat(review.getContent()).isEqualTo("안좋은 스터디");
    }

    @Test
    @DisplayName("스터디 리뷰 작성자인지 확인한다")
    void isWriter() {
        // when
        boolean actual1 = review.isWriter(memberA.getId());
        boolean actual2 = review.isWriter(memberB.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
