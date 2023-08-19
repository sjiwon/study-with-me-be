package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.adapter.StudyReadAdapter;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantReadAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApproveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.event.StudyApprovedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> ApproveParticipationService 테스트")
class ApproveParticipationServiceTest extends UseCaseTest {
    @InjectMocks
    private ApproveParticipationService approveParticipationService;

    @Mock
    private ParticipantReadAdapter participantReadAdapter;

    @Mock
    private StudyReadAdapter studyReadAdapter;

    @Mock
    private StudyParticipantJpaRepository studyParticipantJpaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member applierWithAllowEmail = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Member applierWithNotAllowEmail = ANONYMOUS.toMember().apply(3L, LocalDateTime.now());
    private Study study;
    private int previousParticipantMembers;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
        previousParticipantMembers = 1; // host
    }

    @Test
    @DisplayName("스터디 신청자가 아닌 사용자에 대해서 참여 승인을 할 수 없다")
    void throwExceptionByApplierNotFound() {
        // given
        doThrow(StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND))
                .when(participantReadAdapter)
                .getApplier(any(), any());

        // when - then
        assertThatThrownBy(() -> approveParticipationService.invoke(
                new ApproveParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(participantReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReadAdapter, times(0)).getById(any()),
                () -> verify(studyParticipantJpaRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyApprovedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디가 종료됨에 따라 참여 승인을 할 수 없다")
    void throwExceptionByStudyIsTerminated() {
        // given
        study.terminate();
        given(participantReadAdapter.getApplier(any(), any())).willReturn(applierWithAllowEmail);
        given(studyReadAdapter.getById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> approveParticipationService.invoke(
                new ApproveParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_IS_TERMINATED.getMessage());

        assertAll(
                () -> verify(participantReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReadAdapter, times(1)).getById(any()),
                () -> verify(studyParticipantJpaRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyApprovedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 정원이 꽉 찼기 때문에 추가적인 참여 승인을 할 수 없다")
    void throwExceptionByStudyCapacityIsFull() {
        // given
        given(participantReadAdapter.getApplier(any(), any())).willReturn(applierWithAllowEmail);
        given(studyReadAdapter.getById(any())).willReturn(study);

        final int capacity = study.getCapacity().getValue();
        for (int i = 0; i < capacity - 1; i++) {
            study.addParticipant();
        }

        // when - then
        assertThatThrownBy(() -> approveParticipationService.invoke(
                new ApproveParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_CAPACITY_ALREADY_FULL.getMessage());

        assertAll(
                () -> verify(participantReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReadAdapter, times(1)).getById(any()),
                () -> verify(studyParticipantJpaRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyApprovedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 참여를 승인한다 [이메일 수신 동의에 의한 이메일 발송 이벤트 O]")
    void successA() {
        // given
        given(participantReadAdapter.getApplier(any(), any())).willReturn(applierWithAllowEmail);
        given(studyReadAdapter.getById(any())).willReturn(study);

        // when
        approveParticipationService.invoke(
                new ApproveParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId()
                )
        );

        // then
        assertAll(
                () -> verify(participantReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReadAdapter, times(1)).getById(any()),
                () -> verify(studyParticipantJpaRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(StudyApprovedEvent.class)),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers + 1)
        );
    }

    @Test
    @DisplayName("스터디 참여를 승인한다 [이메일 수신 비동의에 의한 이메일 발송 이벤트 X]")
    void successB() {
        // given
        given(participantReadAdapter.getApplier(any(), any())).willReturn(applierWithNotAllowEmail);
        given(studyReadAdapter.getById(any())).willReturn(study);

        // when
        approveParticipationService.invoke(
                new ApproveParticipationUseCase.Command(
                        study.getId(),
                        applierWithNotAllowEmail.getId()
                )
        );

        // then
        assertAll(
                () -> verify(participantReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReadAdapter, times(1)).getById(any()),
                () -> verify(studyParticipantJpaRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyApprovedEvent.class)),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers + 1)
        );
    }
}
