package com.kgu.studywithme.studyreview.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyReview -> StudyReviewRepository 테스트")
class StudyReviewJpaRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyReviewJpaRepository studyReviewJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    private Member member;
    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(JIWON.toMember());
        studyA = studyJpaRepository.save(SPRING.toOnlineStudy(member.getId()));
        studyB = studyJpaRepository.save(JPA.toOnlineStudy(member.getId()));
    }

    @Test
    @DisplayName("사용자가 해당 스터디에 대해서 작성한 리뷰가 존재하는지 확인한다")
    void existsByStudyIdAndWriterId() {
        /* studyA 리뷰 */
        studyReviewJpaRepository.save(
                StudyReview.writeReview(studyA.getId(), member.getId(), "studyA 리뷰")
        );

        assertAll(
                () -> assertThat(studyReviewJpaRepository.existsByStudyIdAndWriterId(studyA.getId(), member.getId())).isTrue(),
                () -> assertThat(studyReviewJpaRepository.existsByStudyIdAndWriterId(studyB.getId(), member.getId())).isFalse()
        );

        /* studyA + studyB 리뷰 */
        studyReviewJpaRepository.save(
                StudyReview.writeReview(studyB.getId(), member.getId(), "studyB 리뷰")
        );

        assertAll(
                () -> assertThat(studyReviewJpaRepository.existsByStudyIdAndWriterId(studyA.getId(), member.getId())).isTrue(),
                () -> assertThat(studyReviewJpaRepository.existsByStudyIdAndWriterId(studyB.getId(), member.getId())).isTrue()
        );
    }
}