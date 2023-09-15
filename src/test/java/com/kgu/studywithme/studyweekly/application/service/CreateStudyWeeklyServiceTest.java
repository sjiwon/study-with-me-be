package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.event.WeeklyCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> CreateStudyWeeklyService 테스트")
class CreateStudyWeeklyServiceTest extends UseCaseTest {
    @InjectMocks
    private CreateStudyWeeklyService createStudyWeeklyService;

    @Mock
    private StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;

    @Mock
    private StudyWeeklyRepository studyWeeklyRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("스터디 주차를 생성한다")
    void createWeekly() {
        // given
        given(studyWeeklyHandlingRepositoryAdapter.getNextWeek(any())).willReturn(1);

        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L, LocalDateTime.now());
        given(studyWeeklyRepository.save(any())).willReturn(weekly);

        // when
        final Long createdWeeklyId = createStudyWeeklyService.invoke(
                new CreateStudyWeeklyUseCase.Command(
                        study.getId(),
                        host.getId(),
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        STUDY_WEEKLY_1.getPeriod().toPeriod(),
                        STUDY_WEEKLY_1.isAssignmentExists(),
                        STUDY_WEEKLY_1.isAutoAttendance(),
                        STUDY_WEEKLY_1.getAttachments()
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).getNextWeek(any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(WeeklyCreatedEvent.class)),
                () -> assertThat(createdWeeklyId).isEqualTo(weekly.getId())
        );
    }
}
