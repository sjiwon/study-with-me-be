package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.event.AssignmentEditedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> EditWeeklyAssignmentService 테스트")
class EditWeeklyAssignmentServiceTest extends UseCaseTest {
    @InjectMocks
    private EditWeeklyAssignmentService editWeeklyAssignmentService;

    @Mock
    private StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
            .apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("이전에 제출한 과제가 없다면 수정할 수 없다")
    void throwExceptionBySubmittedAssignmentNotFound() {
        // given
        given(studyWeeklyHandlingRepositoryAdapter.getSubmittedAssignment(any(), any(), any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> editWeeklyAssignmentService.invoke(
                new EditWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        UploadAssignment.withLink("https://notion.so")
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(AssignmentEditedEvent.class))
        );
    }

    @Test
    @DisplayName("제출한 과제를 수정한다")
    void success() {
        // given
        final StudyWeeklySubmit submittedAssignment = StudyWeeklySubmit.submitAssignment(
                weekly,
                host.getId(),
                UploadAssignment.withLink("https://notion.so")
        ).apply(1L, LocalDateTime.now());
        given(studyWeeklyHandlingRepositoryAdapter.getSubmittedAssignment(any(), any(), any())).willReturn(Optional.of(submittedAssignment));

        // when
        editWeeklyAssignmentService.invoke(
                new EditWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        UploadAssignment.withFile("hello3.pdf", "https://notion.so/hello3.pdf")
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyHandlingRepositoryAdapter, times(1)).getSubmittedAssignment(any(), any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(AssignmentEditedEvent.class)),
                () -> assertThat(submittedAssignment.getUploadAssignment().getSubmitType()).isEqualTo(FILE),
                () -> assertThat(submittedAssignment.getUploadAssignment().getUploadFileName()).isEqualTo("hello3.pdf"),
                () -> assertThat(submittedAssignment.getUploadAssignment().getLink()).isEqualTo("https://notion.so/hello3.pdf")
        );
    }
}
