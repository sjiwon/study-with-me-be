package com.kgu.studywithme.studyweekly.domain.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.query.dto.AutoAttendanceAndFinishedWeekly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
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
        studyA = studyRepository.save(SPRING.toStudy(host));
        studyB = studyRepository.save(JPA.toStudy(host));
    }

    @Test
    @DisplayName("자동 출석이 적용된 주차 중 주어진 기간 사이에 과제 제출 기간이 종료된 Weekly를 조회한다")
    void findAutoAttendanceAndFinishedWeekly() {
        final LocalDateTime now = LocalDateTime.now();

        studyWeeklyRepository.saveAll(List.of(
                StudyWeekly.createWeeklyWithAssignment(
                        studyA,
                        host,
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getWeek(),
                        new Period(now.minusDays(10), now.minusDays(5)),
                        true,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studyB,
                        host,
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getWeek(),
                        new Period(now.minusDays(8), now.minusDays(2)),
                        true,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studyA,
                        host,
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        STUDY_WEEKLY_2.getWeek(),
                        new Period(now.minusDays(4), now.minusDays(1)),
                        true,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studyB,
                        host,
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        STUDY_WEEKLY_2.getWeek(),
                        new Period(now.minusDays(1), now.plusDays(3)),
                        true,
                        List.of()
                )
        ));

        /* now-7 ~ now-3 -> studyA's Weekly1 */
        final List<AutoAttendanceAndFinishedWeekly> result1 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly(
                now.minusDays(7),
                now.minusDays(3)
        );
        assertAll(
                () -> assertThat(result1).hasSize(1),
                () -> assertThat(result1)
                        .map(AutoAttendanceAndFinishedWeekly::studyId)
                        .containsExactly(studyA.getId()),
                () -> assertThat(result1)
                        .map(AutoAttendanceAndFinishedWeekly::week)
                        .containsExactly(STUDY_WEEKLY_1.getWeek())
        );

        /* now-3 ~ now+1 -> studyA's Weekly2, studyB's Weekly1 */
        final List<AutoAttendanceAndFinishedWeekly> result2 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly(
                now.minusDays(3),
                now.plusDays(1)
        );
        assertAll(
                () -> assertThat(result2).hasSize(2),
                () -> assertThat(result2)
                        .map(AutoAttendanceAndFinishedWeekly::studyId)
                        .containsExactly(studyA.getId(), studyB.getId()),
                () -> assertThat(result2)
                        .map(AutoAttendanceAndFinishedWeekly::week)
                        .containsExactly(STUDY_WEEKLY_2.getWeek(), STUDY_WEEKLY_1.getWeek())
        );

        /* now+1 ~ now+5 -> studyB's Weekly2 */
        final List<AutoAttendanceAndFinishedWeekly> result3 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly(
                now.plusDays(1),
                now.plusDays(5)
        );
        assertAll(
                () -> assertThat(result3).hasSize(1),
                () -> assertThat(result3)
                        .map(AutoAttendanceAndFinishedWeekly::studyId)
                        .containsExactly(studyB.getId()),
                () -> assertThat(result3)
                        .map(AutoAttendanceAndFinishedWeekly::week)
                        .containsExactly(STUDY_WEEKLY_2.getWeek())
        );

        /* now+5 ~ now+9 -> empty */
        final List<AutoAttendanceAndFinishedWeekly> result4 = studyWeeklyHandlingRepository.findAutoAttendanceAndFinishedWeekly(
                now.plusDays(5),
                now.plusDays(9)
        );
        assertThat(result4).isEmpty();
    }
}
