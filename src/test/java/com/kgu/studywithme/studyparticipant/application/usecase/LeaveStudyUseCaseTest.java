package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveStudyCommand;
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
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> LeaveStudyUseCase 테스트")
class LeaveStudyUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final ParticipationInspector participationInspector = new ParticipationInspector(studyParticipantRepository, studyAttendanceRepository);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ParticipationProcessor participationProcessor = new ParticipationProcessor(studyParticipantRepository, eventPublisher);
    private final LeaveStudyUseCase sut = new LeaveStudyUseCase(studyRepository, participateMemberReader, participationInspector, participationProcessor);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member participant = GHOST.toMember().apply(2L);
    private final Member anonymous = ANONYMOUS.toMember().apply(3L);
    private Study study;
    private int previousParticipantMembers;
    private LeaveStudyCommand hostLeaveCommand;
    private LeaveStudyCommand anonymousLeaveCommand;
    private LeaveStudyCommand participantLeaveCommand;

    @BeforeEach
    void setUp() {
        study = SPRING.toStudy(host.getId()).apply(1L);
        previousParticipantMembers = study.getParticipants();

        hostLeaveCommand = new LeaveStudyCommand(study.getId(), host.getId());
        anonymousLeaveCommand = new LeaveStudyCommand(study.getId(), anonymous.getId());
        participantLeaveCommand = new LeaveStudyCommand(study.getId(), participant.getId());
    }

    @Test
    @DisplayName("스터디 참여자가 아니면 스터디를 떠날 수 없다")
    void throwExceptionByMemberIsNotParticipant() {
        // given
        given(studyRepository.getById(anonymousLeaveCommand.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(anonymousLeaveCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(anonymousLeaveCommand.studyId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(anonymousLeaveCommand.studyId(), anonymousLeaveCommand.participantId(), LEAVE)
        );
    }

    @Test
    @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않으면 스터디를 떠날 수 없다")
    void throwExceptionByHostCannotLeaveStudy() {
        // given
        given(studyRepository.getById(hostLeaveCommand.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(hostLeaveCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(hostLeaveCommand.studyId()),
                () -> verify(studyParticipantRepository, times(0))
                        .updateParticipantStatus(hostLeaveCommand.studyId(), hostLeaveCommand.participantId(), LEAVE)
        );
    }

    @Test
    @DisplayName("스터디를 떠난다")
    void success() {
        // given
        given(studyRepository.getById(participantLeaveCommand.studyId())).willReturn(study);

        // when
        sut.invoke(participantLeaveCommand);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(participantLeaveCommand.studyId()),
                () -> verify(studyParticipantRepository, times(1))
                        .updateParticipantStatus(participantLeaveCommand.studyId(), participantLeaveCommand.participantId(), LEAVE),
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

                if (memberId.equals(participant.getId())) {
                    return participant;
                }

                throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND);
            }
        };
    }
}
