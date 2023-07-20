package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudyWeekly -> StudyWeeklyHandlingRepository 테스트")
class StudyWeeklyHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("스터디 다음 주차를 조회한다")
    void getNextWeek() {
        /* 0주차 -> 1 */
        final int nextWeek1 = studyWeeklyRepository.getNextWeek(study.getId());
        assertThat(nextWeek1).isEqualTo(1);

        /* 1주차 -> 2 */
        studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId()));

        final int nextWeek2 = studyWeeklyRepository.getNextWeek(study.getId());
        assertThat(nextWeek2).isEqualTo(2);

        /* 2주차 -> 3 */
        studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(study.getId(), host.getId()));

        final int nextWeek3 = studyWeeklyRepository.getNextWeek(study.getId());
        assertThat(nextWeek3).isEqualTo(3);
    }
}
