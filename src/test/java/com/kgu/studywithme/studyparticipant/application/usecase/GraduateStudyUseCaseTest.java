package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyCommand;
import com.kgu.studywithme.studyparticipant.domain.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationInspector;
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
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> GraduateStudyUseCase 테스트")
class GraduateStudyUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final ParticipationInspector participationInspector
            = new ParticipationInspector(studyParticipantRepository, studyAttendanceRepository);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ParticipationProcessor participationProcessor
            = new ParticipationProcessor(studyParticipantRepository, eventPublisher);
    private final GraduateStudyUseCase sut
            = new GraduateStudyUseCase(studyRepository, participateMemberReader, participationInspector, participationProcessor);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member participantWithAllowEmail = GHOST.toMember().apply(2L);
    private final Member participantWithNotAllowEmail = ANONYMOUS.toMember().apply(3L);
    private final Member anonymous = DUMMY1.toMember().apply(4L);
    private Study study;
    private int previousParticipantMembers;
    private GraduateStudyCommand hostGraduateCommand;
    private GraduateStudyCommand anonymousGraduateCommand;
    private GraduateStudyCommand allowEmailParticipantCommand;
    private GraduateStudyCommand notAllowEmailParticipantCommand;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L);
        previousParticipantMembers = study.getParticipants();

        hostGraduateCommand = new GraduateStudyCommand(study.getId(), host.getId());
        anonymousGraduateCommand = new GraduateStudyCommand(study.getId(), anonymous.getId());
        allowEmailParticipantCommand = new GraduateStudyCommand(study.getId(), participantWithAllowEmail.getId());
        notAllowEmailParticipantCommand = new GraduateStudyCommand(study.getId(), participantWithNotAllowEmail.getId());
    }

    @Test
    @DisplayName("스터디 참여자가 아니면 스터디를 졸업할 수 없다")
    void throwExceptionByMemberIsNotParticipant() {
        // given
        given(studyRepository.getById(anonymousGraduateCommand.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(anonymousGraduateCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(anonymousGraduateCommand.studyId()),
                () -> verify(studyAttendanceRepository, times(0))
                        .getAttendanceStatusCount(anonymousGraduateCommand.studyId(), anonymousGraduateCommand.participantId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(anonymousGraduateCommand.studyId(), anonymousGraduateCommand.participantId(), GRADUATED),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않으면 스터디를 졸업할 수 없다")
    void throwExceptionByHostCannotGraduateStudy() {
        // given
        given(studyRepository.getById(hostGraduateCommand.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(hostGraduateCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(hostGraduateCommand.studyId()),
                () -> verify(studyAttendanceRepository, times(0))
                        .getAttendanceStatusCount(hostGraduateCommand.studyId(), hostGraduateCommand.participantId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(hostGraduateCommand.studyId(), hostGraduateCommand.participantId(), GRADUATED),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class))
        );
    }

    @Test
    @DisplayName("졸업 요건을 만족하지 못한 참여자는 스터디를 졸업할 수 없다")
    void throwExceptionByParticipantNotMeetGraduationPolicy() {
        // given
        given(studyRepository.getById(allowEmailParticipantCommand.studyId())).willReturn(study);
        given(studyAttendanceRepository.getAttendanceStatusCount(allowEmailParticipantCommand.studyId(), allowEmailParticipantCommand.participantId()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance() - 1);

        // when - then
        assertThatThrownBy(() -> sut.invoke(allowEmailParticipantCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(allowEmailParticipantCommand.studyId()),
                () -> verify(studyAttendanceRepository, times(1))
                        .getAttendanceStatusCount(allowEmailParticipantCommand.studyId(), allowEmailParticipantCommand.participantId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(allowEmailParticipantCommand.studyId(), allowEmailParticipantCommand.participantId(), GRADUATED),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디를 졸업한다 [이메일 수신 동의에 의한 이메일 발송 이벤트 O]")
    void successA() {
        // given
        given(studyRepository.getById(allowEmailParticipantCommand.studyId())).willReturn(study);
        given(studyAttendanceRepository.getAttendanceStatusCount(allowEmailParticipantCommand.studyId(), allowEmailParticipantCommand.participantId()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance());

        // when
        sut.invoke(allowEmailParticipantCommand);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(allowEmailParticipantCommand.studyId()),
                () -> verify(studyAttendanceRepository, times(1))
                        .getAttendanceStatusCount(allowEmailParticipantCommand.studyId(), allowEmailParticipantCommand.participantId()),
                () -> verify(studyParticipantRepository, times(1))
                        .updateParticipantStatus(allowEmailParticipantCommand.studyId(), allowEmailParticipantCommand.participantId(), GRADUATED),
                () -> verify(eventPublisher, times(1)).publishEvent(any(StudyGraduatedEvent.class)),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
        );
    }

    @Test
    @DisplayName("스터디를 졸업한다 [이메일 수신 비동의에 의한 이메일 발송 이벤트 X]")
    void successB() {
        // given
        given(studyRepository.getById(notAllowEmailParticipantCommand.studyId())).willReturn(study);
        given(studyAttendanceRepository.getAttendanceStatusCount(notAllowEmailParticipantCommand.studyId(), notAllowEmailParticipantCommand.participantId()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance());

        // when
        sut.invoke(notAllowEmailParticipantCommand);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(notAllowEmailParticipantCommand.studyId()),
                () -> verify(studyAttendanceRepository, times(1))
                        .getAttendanceStatusCount(notAllowEmailParticipantCommand.studyId(), notAllowEmailParticipantCommand.participantId()),
                () -> verify(studyParticipantRepository, times(1))
                        .updateParticipantStatus(notAllowEmailParticipantCommand.studyId(), notAllowEmailParticipantCommand.participantId(), GRADUATED),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class)),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
        );
    }

    private ParticipateMemberReader fakeParticipateMemberReader() {
        return new ParticipateMemberReader() {
            @Override
            public Member getApplier(final Long studyId, final Long memberId) {
                throw StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND);
            }

            @Override
            public Member getParticipant(final Long studyId, final Long memberId) {
                if (memberId.equals(host.getId())) {
                    return host;
                }

                if (memberId.equals(participantWithAllowEmail.getId())) {
                    return participantWithAllowEmail;
                }

                if (memberId.equals(participantWithNotAllowEmail.getId())) {
                    return participantWithNotAllowEmail;
                }

                throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND);
            }
        };
    }
}
