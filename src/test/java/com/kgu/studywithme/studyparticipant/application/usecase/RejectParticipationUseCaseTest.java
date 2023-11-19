package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationCommand;
import com.kgu.studywithme.studyparticipant.domain.event.StudyRejectedEvent;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationProcessor;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> RejectParticipationUseCase 테스트")
class RejectParticipationUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ParticipationProcessor participationProcessor = new ParticipationProcessor(studyParticipantRepository, eventPublisher);
    private final RejectParticipationUseCase sut = new RejectParticipationUseCase(studyRepository, participateMemberReader, participationProcessor);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member applierWithAllowEmail = GHOST.toMember().apply(2L);
    private final Member applierWithNotAllowEmail = ANONYMOUS.toMember().apply(3L);
    private final Member anonymous = DUMMY1.toMember().apply(4L);
    private Study study;
    private RejectParticipationCommand allowEmailMemberCommand;
    private RejectParticipationCommand notAllowEmailMemberCommand;
    private RejectParticipationCommand anonymousCommand;

    @BeforeEach
    void setUp() {
        study = SPRING.toStudy(host).apply(1L);

        allowEmailMemberCommand = new RejectParticipationCommand(study.getId(), applierWithAllowEmail.getId(), "sorry");
        notAllowEmailMemberCommand = new RejectParticipationCommand(study.getId(), applierWithNotAllowEmail.getId(), "sorry");
        anonymousCommand = new RejectParticipationCommand(study.getId(), anonymous.getId(), "sorry");
    }

    @Test
    @DisplayName("스터디가 종료됨에 따라 참여 거절을 할 수 없다")
    void throwExceptionByStudyIsTerminated() {
        // given
        doThrow(StudyWithMeException.type(StudyErrorCode.STUDY_IS_TERMINATED))
                .when(studyRepository)
                .getInProgressStudy(allowEmailMemberCommand.studyId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(allowEmailMemberCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_IS_TERMINATED.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getInProgressStudy(allowEmailMemberCommand.studyId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(allowEmailMemberCommand.studyId(), allowEmailMemberCommand.applierId(), REJECT),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 신청자가 아닌 사용자에 대해서 참여 거절을 할 수 없다")
    void throwExceptionByApplierNotFound() {
        // given
        given(studyRepository.getInProgressStudy(anonymousCommand.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(anonymousCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getInProgressStudy(anonymousCommand.studyId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(anonymousCommand.studyId(), anonymousCommand.applierId(), REJECT),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 참여를 거절한다 [이메일 수신 동의에 의한 이메일 발송 이벤트 O]")
    void successA() {
        // given
        given(studyRepository.getInProgressStudy(allowEmailMemberCommand.studyId())).willReturn(study);

        // when
        sut.invoke(allowEmailMemberCommand);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getInProgressStudy(allowEmailMemberCommand.studyId()),
                () -> verify(studyParticipantRepository, times(1))
                        .updateParticipantStatus(allowEmailMemberCommand.studyId(), allowEmailMemberCommand.applierId(), REJECT),
                () -> verify(eventPublisher, times(1)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 참여를 거절한다 [이메일 수신 비동의에 의한 이메일 발송 이벤트 X]")
    void successB() {
        // given
        given(studyRepository.getInProgressStudy(notAllowEmailMemberCommand.studyId())).willReturn(study);

        // when
        sut.invoke(notAllowEmailMemberCommand);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getInProgressStudy(notAllowEmailMemberCommand.studyId()),
                () -> verify(studyParticipantRepository, times(1))
                        .updateParticipantStatus(notAllowEmailMemberCommand.studyId(), notAllowEmailMemberCommand.applierId(), REJECT),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
        );
    }

    private ParticipateMemberReader fakeParticipateMemberReader() {
        return new ParticipateMemberReader() {
            @Override
            public Member getApplier(final Long studyId, final Long memberId) {
                if (memberId.equals(applierWithAllowEmail.getId())) {
                    return applierWithAllowEmail;
                }

                if (memberId.equals(applierWithNotAllowEmail.getId())) {
                    return applierWithNotAllowEmail;
                }

                throw StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND);
            }

            @Override
            public Member getParticipant(final Long studyId, final Long memberId) {
                return host;
            }
        };
    }
}
