package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> WeeklyUpdater 테스트")
class WeeklyUpdaterTest {
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final WeeklyUpdater sut = new WeeklyUpdater(studyWeeklyRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(host).apply(1L);
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study, host);

    @Test
    @DisplayName("특정 주차 정보를 수정한다")
    void updateWeekly() {
        // given
        given(studyWeeklyRepository.getById(weekly.getId())).willReturn(weekly);

        // when
        sut.invoke(
                weekly.getId(),
                STUDY_WEEKLY_2.getTitle(),
                STUDY_WEEKLY_2.getContent(),
                STUDY_WEEKLY_2.getPeriod().toPeriod(),
                STUDY_WEEKLY_2.isAssignmentExists(),
                STUDY_WEEKLY_2.isAutoAttendance(),
                STUDY_WEEKLY_2.getAttachments()
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getById(weekly.getId()),
                () -> assertThat(weekly.getTitle()).isEqualTo(STUDY_WEEKLY_2.getTitle()),
                () -> assertThat(weekly.getContent()).isEqualTo(STUDY_WEEKLY_2.getContent()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isEqualTo(STUDY_WEEKLY_2.isAssignmentExists()),
                () -> assertThat(weekly.isAutoAttendance()).isEqualTo(STUDY_WEEKLY_2.isAutoAttendance()),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_2.getAttachments())
        );
    }
}
