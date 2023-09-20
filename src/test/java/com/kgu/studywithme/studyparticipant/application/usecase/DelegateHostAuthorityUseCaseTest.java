package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.GraduationPolicy;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.DelegateHostAuthorityCommand;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationInspector;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> DelegateHostAuthorityUseCase 테스트")
class DelegateHostAuthorityUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final ParticipationInspector participationInspector = new ParticipationInspector(studyParticipantRepository, studyAttendanceRepository);
    private final DelegateHostAuthorityUseCase sut = new DelegateHostAuthorityUseCase(studyRepository, participateMemberReader, participationInspector);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member newHost = GHOST.toMember().apply(2L);
    private final Member anonymous = ANONYMOUS.toMember().apply(3L);
    private Study study;
    private DelegateHostAuthorityCommand selfCommand;
    private DelegateHostAuthorityCommand anonymousCommand;
    private DelegateHostAuthorityCommand command;

    @BeforeEach
    void setUp() {
        study = SPRING.toStudy(host.getId()).apply(1L);

        selfCommand = new DelegateHostAuthorityCommand(study.getId(), host.getId());
        anonymousCommand = new DelegateHostAuthorityCommand(study.getId(), anonymous.getId());
        command = new DelegateHostAuthorityCommand(study.getId(), newHost.getId());
    }

    @Test
    @DisplayName("스터디가 종료되었으면 팀장 권한을 위임할 수 없다")
    void throwExceptionByStudyIsTerminated() {
        // given
        doThrow(StudyWithMeException.type(StudyErrorCode.STUDY_IS_TERMINATED))
                .when(studyRepository)
                .getInProgressStudy(command.studyId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_IS_TERMINATED.getMessage());

        verify(studyRepository, times(1)).getInProgressStudy(command.studyId());
    }

    @Test
    @DisplayName("스터디 참여자가 아니면 팀장 권한을 위임할 수 없다")
    void throwExceptionByNewHostIsNotParticipant() {
        // given
        given(studyRepository.getInProgressStudy(command.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(anonymousCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage());

        verify(studyRepository, times(1)).getInProgressStudy(command.studyId());
    }

    @Test
    @DisplayName("팀장 권한을 기존 팀장(Self Invoke)에게 위임할 수 없다")
    void throwExceptionByNewHostIsCurrentHost() {
        // given
        given(studyRepository.getInProgressStudy(command.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(selfCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.SELF_DELEGATING_NOT_ALLOWED.getMessage());

        verify(studyRepository, times(1)).getInProgressStudy(command.studyId());
    }

    @Test
    @DisplayName("팀장 권한을 위임한다 -> 졸업 요건 수정 기회 초기화")
    void success() {
        // given
        given(studyRepository.getInProgressStudy(command.studyId())).willReturn(study);

        ReflectionTestUtils.setField(study.getGraduationPolicy(), "updateChance", 1);
        assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(1);

        // when - then
        sut.invoke(command);

        assertAll(
                () -> verify(studyRepository, times(1)).getInProgressStudy(command.studyId()),
                () -> assertThat(study.getHostId()).isEqualTo(command.newHostId()),
                () -> assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE)
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

                if (memberId.equals(newHost.getId())) {
                    return newHost;
                }

                throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND);
            }
        };
    }
}
