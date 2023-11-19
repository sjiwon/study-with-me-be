package com.kgu.studywithme.studyreview.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
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
class StudyReviewRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyReviewRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member member;
    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        studyA = studyRepository.save(SPRING.toStudy(member));
        studyB = studyRepository.save(JPA.toStudy(member));
    }

    @Test
    @DisplayName("사용자가 해당 스터디에 대해서 작성한 리뷰가 존재하는지 확인한다")
    void existsByStudyIdAndWriterId() {
        /* studyA 리뷰 */
        sut.save(StudyReview.writeReview(studyA, member, "studyA 리뷰"));

        assertAll(
                () -> assertThat(sut.existsByStudyIdAndWriterId(studyA.getId(), member.getId())).isTrue(),
                () -> assertThat(sut.existsByStudyIdAndWriterId(studyB.getId(), member.getId())).isFalse()
        );

        /* studyA + studyB 리뷰 */
        sut.save(StudyReview.writeReview(studyB, member, "studyB 리뷰"));

        assertAll(
                () -> assertThat(sut.existsByStudyIdAndWriterId(studyA.getId(), member.getId())).isTrue(),
                () -> assertThat(sut.existsByStudyIdAndWriterId(studyB.getId(), member.getId())).isTrue()
        );
    }
}
