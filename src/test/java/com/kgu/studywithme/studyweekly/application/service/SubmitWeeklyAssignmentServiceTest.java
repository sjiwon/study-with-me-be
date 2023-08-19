package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantReadAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.event.AssignmentSubmittedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
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
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> SubmitWeeklyAssignmentService 테스트")
class SubmitWeeklyAssignmentServiceTest extends UseCaseTest {
    @InjectMocks
    private SubmitWeeklyAssignmentService submitWeeklyAssignmentService;

    @Mock
    private StudyWeeklyJpaRepository studyWeeklyJpaRepository;

    @Mock
    private ParticipantReadAdapter participantReadAdapter;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
            .apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 과제 제출에 실패한다")
    void throwExceptionByMissingSubmission() {
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        LINK,
                        null,
                        null
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.MISSING_SUBMISSION.getMessage());

        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(0)).findById(any()),
                () -> verify(participantReadAdapter, times(0)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(AssignmentSubmittedEvent.class))
        );
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 과제 제출에 실패한다")
    void throwExceptionByDuplicateSubmission() {
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        LINK,
                        UploadAssignment.withFile("hello.png", "https://hello.png"),
                        "https://notion.so"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION.getMessage());

        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(0)).findById(any()),
                () -> verify(participantReadAdapter, times(0)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(AssignmentSubmittedEvent.class))
        );
    }

    @Test
    @DisplayName("존재하지 않는 주차에 대해서 과제 제출을 하려고 시도하면 실패한다")
    void throwExceptionByWeekNotFound() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        LINK,
                        null,
                        "https://notion.so"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(1)).findById(any()),
                () -> verify(participantReadAdapter, times(0)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(AssignmentSubmittedEvent.class))
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [링크 제출]")
    void successWithLink() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.of(weekly));
        given(participantReadAdapter.getParticipant(any(), any())).willReturn(host);

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        LINK,
                        null,
                        "https://notion.so"
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(1)).findById(any()),
                () -> verify(participantReadAdapter, times(1)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(AssignmentSubmittedEvent.class))
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [파일 제출]")
    void successWithFile() {
        // given
        given(studyWeeklyJpaRepository.findById(any())).willReturn(Optional.of(weekly));
        given(participantReadAdapter.getParticipant(any(), any())).willReturn(host);

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        FILE,
                        UploadAssignment.withFile("hello.png", "https://hello.png"),
                        null
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyJpaRepository, times(1)).findById(any()),
                () -> verify(participantReadAdapter, times(1)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(AssignmentSubmittedEvent.class))
        );
    }
}
