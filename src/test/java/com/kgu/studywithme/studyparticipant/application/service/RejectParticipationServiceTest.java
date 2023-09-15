package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.application.service.StudyReader;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipateMemberReadAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.event.StudyRejectedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> RejectParticipationService 테스트")
class RejectParticipationServiceTest extends UseCaseTest {
    @InjectMocks
    private RejectParticipationService rejectParticipationService;

    @Mock
    private ParticipateMemberReadAdapter participateMemberReadAdapter;

    @Mock
    private StudyReader studyReader;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member applierWithAllowEmail = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Member applierWithNotAllowEmail = ANONYMOUS.toMember().apply(3L, LocalDateTime.now());
    private Study study;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("스터디 신청자가 아닌 사용자에 대해서 참여 거절을 할 수 없다")
    void throwExceptionByApplierNotFound() {
        // given
        doThrow(StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND))
                .when(participateMemberReadAdapter)
                .getApplier(any(), any());

        // when - then
        assertThatThrownBy(() -> rejectParticipationService.invoke(
                new RejectParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId(),
                        "열정 온도가 너무 낮아요 ㅠ"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(participateMemberReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReader, times(0)).getById(any()),
                () -> verify(studyParticipantRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디가 종료됨에 따라 참여 거절을 할 수 없다")
    void throwExceptionByStudyIsTerminated() {
        // given
        study.terminate();
        given(participateMemberReadAdapter.getApplier(any(), any())).willReturn(applierWithAllowEmail);
        given(studyReader.getById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> rejectParticipationService.invoke(
                new RejectParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId(),
                        "열정 온도가 너무 낮아요 ㅠ"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_IS_TERMINATED.getMessage());

        assertAll(
                () -> verify(participateMemberReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReader, times(1)).getById(any()),
                () -> verify(studyParticipantRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 참여를 거절한다 [이메일 수신 동의에 의한 이메일 발송 이벤트 O]")
    void successA() {
        // given
        given(participateMemberReadAdapter.getApplier(any(), any())).willReturn(applierWithAllowEmail);
        given(studyReader.getById(any())).willReturn(study);

        // when
        rejectParticipationService.invoke(
                new RejectParticipationUseCase.Command(
                        study.getId(),
                        applierWithAllowEmail.getId(),
                        "열정 온도가 너무 낮아요 ㅠ"
                )
        );

        // then
        assertAll(
                () -> verify(participateMemberReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReader, times(1)).getById(any()),
                () -> verify(studyParticipantRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 참여를 거절한다 [이메일 수신 비동의에 의한 이메일 발송 이벤트 X]")
    void successB() {
        // given
        given(participateMemberReadAdapter.getApplier(any(), any())).willReturn(applierWithNotAllowEmail);
        given(studyReader.getById(any())).willReturn(study);

        // when
        rejectParticipationService.invoke(
                new RejectParticipationUseCase.Command(
                        study.getId(),
                        applierWithNotAllowEmail.getId(),
                        "열정 온도가 너무 낮아요 ㅠ"
                )
        );

        // then
        assertAll(
                () -> verify(participateMemberReadAdapter, times(1)).getApplier(any(), any()),
                () -> verify(studyReader, times(1)).getById(any()),
                () -> verify(studyParticipantRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
        );
    }
}
