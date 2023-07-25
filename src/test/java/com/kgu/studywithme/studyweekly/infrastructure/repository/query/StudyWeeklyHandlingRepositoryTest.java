package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.infrastructure.repository.query.dto.AutoAttendanceAndFinishedWeekly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.PeriodFixture.WEEK_0;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly -> StudyWeeklyHandlingRepository 테스트")
class StudyWeeklyHandlingRepositoryTest extends RepositoryTest {
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
    @DisplayName("스터디 다음 주차를 조회한다")
    void getNextWeek() {
        /* 0주차 -> 1 */
        final int nextWeek1 = studyWeeklyRepository.getNextWeek(studyA.getId());
        assertThat(nextWeek1).isEqualTo(1);

        /* 1주차 -> 2 */
        studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(studyA.getId(), host.getId()));

        final int nextWeek2 = studyWeeklyRepository.getNextWeek(studyA.getId());
        assertThat(nextWeek2).isEqualTo(2);

        /* 2주차 -> 3 */
        studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(studyA.getId(), host.getId()));

        final int nextWeek3 = studyWeeklyRepository.getNextWeek(studyA.getId());
        assertThat(nextWeek3).isEqualTo(3);
    }

    @Test
    @DisplayName("특정 주차 정보를 조회한다")
    void getSpecificWeekly() {
        /* 0주차 */
        assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 0)).isEmpty();

        /* 1주차 */
        assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 1)).isEmpty();

        studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(studyA.getId(), host.getId()));
        assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 1)).isPresent();
    }

    @Test
    @DisplayName("주어진 주차가 가장 최신 주차인지 확인한다")
    void isLatestWeek() {
        /* 0주차 */
        assertThat(studyWeeklyRepository.isLatestWeek(studyA.getId(), 0)).isTrue();

        /* 1주차 */
        studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(studyA.getId(), host.getId()));
        assertAll(
                () -> assertThat(studyWeeklyRepository.isLatestWeek(studyA.getId(), 0)).isFalse(),
                () -> assertThat(studyWeeklyRepository.isLatestWeek(studyA.getId(), 1)).isTrue()
        );

        /* 2주차 */
        studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(studyA.getId(), host.getId()));
        assertAll(
                () -> assertThat(studyWeeklyRepository.isLatestWeek(studyA.getId(), 0)).isFalse(),
                () -> assertThat(studyWeeklyRepository.isLatestWeek(studyA.getId(), 1)).isFalse(),
                () -> assertThat(studyWeeklyRepository.isLatestWeek(studyA.getId(), 2)).isTrue()
        );
    }

    @Test
    @DisplayName("특정 주차 정보를 삭제 한다 [제출 과제 삭제 -> 주차 첨부파일 삭제 -> 주차 출석정보 삭제 -> 주차 삭제]")
    void deleteSpecificWeekly() {
        studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(studyA.getId(), host.getId()));
        studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(studyA.getId(), host.getId()));
        studyWeeklyRepository.save(STUDY_WEEKLY_3.toWeeklyWithAssignment(studyA.getId(), host.getId()));

        /* 3주차 삭제 */
        studyWeeklyRepository.deleteSpecificWeekly(studyA.getId(), STUDY_WEEKLY_3.getWeek());
        assertAll(
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 1)).isPresent(),
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 2)).isPresent(),
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 3)).isEmpty()
        );

        /* 2주차 삭제 */
        studyWeeklyRepository.deleteSpecificWeekly(studyA.getId(), STUDY_WEEKLY_2.getWeek());
        assertAll(
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 1)).isPresent(),
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 2)).isEmpty(),
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 3)).isEmpty()
        );

        /* 1주차 삭제 */
        studyWeeklyRepository.deleteSpecificWeekly(studyA.getId(), STUDY_WEEKLY_1.getWeek());
        assertAll(
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 1)).isEmpty(),
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 2)).isEmpty(),
                () -> assertThat(studyWeeklyRepository.getSpecificWeekly(studyA.getId(), 3)).isEmpty()
        );
    }

    @Test
    @DisplayName("해당 주차에 제출한 과제를 조회한다")
    void getSubmittedAssignment() {
        /* 1주차 X */
        assertThat(studyWeeklyRepository.getSubmittedAssignment(host.getId(), studyA.getId(), 1)).isEmpty();

        /* 1주차 O */
        final StudyWeekly weekly1 = studyWeeklyRepository.save(
                STUDY_WEEKLY_1.toWeeklyWithAssignment(studyA.getId(), host.getId())
        );
        weekly1.submitAssignment(host.getId(), UploadAssignment.withLink("https://notion.so"));
        assertThat(studyWeeklyRepository.getSubmittedAssignment(host.getId(), studyA.getId(), 1)).isPresent();

        /* 2주차 X */
        assertThat(studyWeeklyRepository.getSubmittedAssignment(host.getId(), studyA.getId(), 2)).isEmpty();

        /* 2주차 O */
        final StudyWeekly weekly2 = studyWeeklyRepository.save(
                STUDY_WEEKLY_2.toWeeklyWithAssignment(studyA.getId(), host.getId())
        );
        weekly2.submitAssignment(host.getId(), UploadAssignment.withLink("https://notion.so"));
        assertThat(studyWeeklyRepository.getSubmittedAssignment(host.getId(), studyA.getId(), 2)).isPresent();
    }

    @Test
    @DisplayName("자동 출석이 적용된 주차 중 과제 제출 기간이 종료된 Weekly를 조회한다")
    void findAutoAttendanceAndFinishedWeekly() {
        final List<AutoAttendanceAndFinishedWeekly> result1 = studyWeeklyRepository.findAutoAttendanceAndFinishedWeekly();
        assertThat(result1).isEmpty();

        /* studyA-Week1 */
        studyWeeklyRepository.saveAll(
                List.of(
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
                )
        );

        final List<AutoAttendanceAndFinishedWeekly> result2 = studyWeeklyRepository.findAutoAttendanceAndFinishedWeekly();
        assertAll(
                () -> assertThat(result2)
                        .map(AutoAttendanceAndFinishedWeekly::studyId)
                        .containsExactly(studyA.getId()),
                () -> assertThat(result2)
                        .map(AutoAttendanceAndFinishedWeekly::week)
                        .containsExactly(STUDY_WEEKLY_1.getWeek())
        );

        /* studyA-Week1 & studyB-Week2 */
        studyWeeklyRepository.saveAll(
                List.of(
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
                )
        );

        final List<AutoAttendanceAndFinishedWeekly> result3 = studyWeeklyRepository.findAutoAttendanceAndFinishedWeekly();
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
