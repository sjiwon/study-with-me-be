package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly -> StudyWeeklyRepository 테스트")
public class StudyWeeklyRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyWeeklyRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toStudy(host));
    }

    @Test
    @DisplayName("스터디 다음 주차를 조회한다")
    void getNextWeek() {
        /* 0주차 -> 1 */
        assertThat(sut.getNextWeek(study.getId())).isEqualTo(1);

        /* 1주차 -> 2 */
        sut.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(study, host));
        assertThat(sut.getNextWeek(study.getId())).isEqualTo(2);

        /* 2주차 -> 3 */
        sut.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(study, host));
        assertThat(sut.getNextWeek(study.getId())).isEqualTo(3);

        /* 3주차 -> 4 */
        sut.save(STUDY_WEEKLY_3.toWeeklyWithAssignment(study, host));
        assertThat(sut.getNextWeek(study.getId())).isEqualTo(4);
    }

    @Test
    @DisplayName("해당 주차가 가장 최신 주차인지 확인한다")
    void isLatestWeek() {
        /* 1주차 */
        final StudyWeekly weekly1 = sut.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(study, host));
        assertThat(sut.isLatestWeek(study.getId(), weekly1.getId())).isTrue();

        /* 2주차 */
        final StudyWeekly weekly2 = sut.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(study, host));
        assertAll(
                () -> assertThat(sut.isLatestWeek(study.getId(), weekly1.getId())).isFalse(),
                () -> assertThat(sut.isLatestWeek(study.getId(), weekly2.getId())).isTrue()
        );

        /* 3주차 */
        final StudyWeekly weekly3 = sut.save(STUDY_WEEKLY_3.toWeeklyWithAssignment(study, host));
        assertAll(
                () -> assertThat(sut.isLatestWeek(study.getId(), weekly1.getId())).isFalse(),
                () -> assertThat(sut.isLatestWeek(study.getId(), weekly2.getId())).isFalse(),
                () -> assertThat(sut.isLatestWeek(study.getId(), weekly3.getId())).isTrue()
        );
    }
}
