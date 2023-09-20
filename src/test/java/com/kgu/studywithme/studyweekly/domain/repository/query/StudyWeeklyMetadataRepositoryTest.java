package com.kgu.studywithme.studyweekly.domain.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.query.dto.AutoAttendanceAndFinishedWeekly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.PeriodFixture.WEEK_0;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyWeeklyMetadataRepositoryImpl.class)
@DisplayName("StudyWeekly -> StudyWeeklyHandlingRepository 테스트")
class StudyWeeklyMetadataRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyWeeklyMetadataRepositoryImpl studyWeeklyHandlingRepository;

    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        studyA = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
        studyB = studyRepository.save(JPA.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("자동 출석이 적용된 주차 중 과제 제출 기간이 종료된 Weekly를 조회한다")
    void findAutoAttendanceAndFinishedWeekly() {
        final List<AutoAttendanceAndFinishedWeekly> result1 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly();
        assertThat(result1).isEmpty();

        /* studyA-Week1 */
        studyWeeklyRepository.saveAll(List.of(
                StudyWeekly.createWeeklyWithAssignment(
                        studyA.getId(),
                        host.getId(),
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getWeek(),
                        WEEK_0.toPeriod(),
                        true,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studyB.getId(),
                        host.getId(),
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getWeek(),
                        WEEK_0.toPeriod(),
                        false,
                        List.of()
                )
        ));

        final List<AutoAttendanceAndFinishedWeekly> result2 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly();
        assertAll(
                () -> assertThat(result2)
                        .map(AutoAttendanceAndFinishedWeekly::studyId)
                        .containsExactly(studyA.getId()),
                () -> assertThat(result2)
                        .map(AutoAttendanceAndFinishedWeekly::week)
                        .containsExactly(STUDY_WEEKLY_1.getWeek())
        );

        /* studyA-Week1 & studyB-Week2 */
        studyWeeklyRepository.saveAll(List.of(
                StudyWeekly.createWeeklyWithAssignment(
                        studyA.getId(),
                        host.getId(),
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        STUDY_WEEKLY_2.getWeek(),
                        WEEK_0.toPeriod(),
                        false,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studyB.getId(),
                        host.getId(),
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        STUDY_WEEKLY_2.getWeek(),
                        WEEK_0.toPeriod(),
                        true,
                        List.of()
                )
        ));

        final List<AutoAttendanceAndFinishedWeekly> result3 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly();
        assertAll(
                () -> assertThat(result3)
                        .map(AutoAttendanceAndFinishedWeekly::studyId)
                        .containsExactly(studyA.getId(), studyB.getId()),
                () -> assertThat(result3)
                        .map(AutoAttendanceAndFinishedWeekly::week)
                        .containsExactly(STUDY_WEEKLY_1.getWeek(), STUDY_WEEKLY_2.getWeek())
        );
    }
}
