package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.event.AssignmentSubmittedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.BeforeEach;
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
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
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
    private StudyWeeklyRepository studyWeeklyRepository;

    @Mock
    private ParticipateMemberReader participateMemberReader;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private StudyWeekly weekly;

    @BeforeEach
    void setUp() {
        weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId()).apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("존재하지 않는 주차에 대해서 과제 제출을 하려고 시도하면 실패한다")
    void throwExceptionByWeekNotFound() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        UploadAssignment.withLink("https://notion.so")
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(participateMemberReader, times(0)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(AssignmentSubmittedEvent.class))
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [링크 제출]")
    void successWithLink() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.of(weekly));
        given(participateMemberReader.getParticipant(any(), any())).willReturn(host);

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        UploadAssignment.withLink("https://notion.so")
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(participateMemberReader, times(1)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(AssignmentSubmittedEvent.class)),
                () -> assertThat(weekly.getSubmits()).hasSize(1),
                () -> {
                    final UploadAssignment assignment = weekly.getSubmits().get(0).getUploadAssignment();
                    assertAll(
                            () -> assertThat(assignment.getSubmitType()).isEqualTo(LINK),
                            () -> assertThat(assignment.getUploadFileName()).isNull(),
                            () -> assertThat(assignment.getLink()).isEqualTo("https://notion.so")
                    );
                }
        );
    }

    @Test
    @DisplayName("해당 주차 과제를 제출한다 [파일 제출]")
    void successWithFile() {
        // given
        given(studyWeeklyRepository.findById(any())).willReturn(Optional.of(weekly));
        given(participateMemberReader.getParticipant(any(), any())).willReturn(host);

        // when
        submitWeeklyAssignmentService.invoke(
                new SubmitWeeklyAssignmentUseCase.Command(
                        host.getId(),
                        study.getId(),
                        weekly.getId(),
                        UploadAssignment.withFile("hello3.pdf", "https://notion.so/hello3.pdf")
                )
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).findById(any()),
                () -> verify(participateMemberReader, times(1)).getParticipant(any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(AssignmentSubmittedEvent.class)),
                () -> assertThat(weekly.getSubmits()).hasSize(1),
                () -> {
                    final UploadAssignment assignment = weekly.getSubmits().get(0).getUploadAssignment();
                    assertAll(
                            () -> assertThat(assignment.getSubmitType()).isEqualTo(FILE),
                            () -> assertThat(assignment.getUploadFileName()).isEqualTo("hello3.pdf"),
                            () -> assertThat(assignment.getLink()).isEqualTo("https://notion.so/hello3.pdf")
                    );
                }
        );
    }
}
