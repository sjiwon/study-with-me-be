package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyCommand;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.domain.service.ParticipationInspector;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> ApplyStudyUseCase 테스트")
class ApplyStudyUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final ParticipationInspector participationInspector
            = new ParticipationInspector(studyParticipantRepository, studyAttendanceRepository);
    private final ApplyStudyUseCase sut
            = new ApplyStudyUseCase(studyRepository, participationInspector, studyParticipantRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member applier = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final ApplyStudyCommand hostCommand = new ApplyStudyCommand(study.getId(), host.getId());
    private final ApplyStudyCommand applierCommand = new ApplyStudyCommand(study.getId(), applier.getId());

    @Test
    @DisplayName("스터디가 모집중이지 않으면 참여 신청을 할 수 없다")
    void throwExceptionByStudyIsNotRecruitingNow() {
        // given
        doThrow(StudyWithMeException.type(StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW))
                .when(studyRepository)
                .getRecruitingStudy(hostCommand.studyId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(hostCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getRecruitingStudy(hostCommand.studyId()),
                () -> verify(studyParticipantRepository, times(0))
                        .isApplierOrParticipant(hostCommand.studyId(), hostCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0))
                        .isAlreadyLeaveOrGraduatedParticipant(hostCommand.studyId(), hostCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("스터디 팀장은 본인 스터디에 참여 신청을 할 수 없다")
    void throwExceptionByStudyHostCannotApplyOwnStudy() {
        // given
        given(studyRepository.getRecruitingStudy(hostCommand.studyId())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> sut.invoke(hostCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getRecruitingStudy(hostCommand.studyId()),
                () -> verify(studyParticipantRepository, times(0))
                        .isApplierOrParticipant(hostCommand.studyId(), hostCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0))
                        .isAlreadyLeaveOrGraduatedParticipant(hostCommand.studyId(), hostCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("이미 신청했거나 참여중인 스터디에 다시 참여 신청할 수 없다")
    void throwExceptionByAlreadyApplyOrParticipate() {
        // given
        given(studyRepository.getRecruitingStudy(applierCommand.studyId())).willReturn(study);
        given(studyParticipantRepository.isApplierOrParticipant(applierCommand.studyId(), applierCommand.applierId()))
                .willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(applierCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getRecruitingStudy(applierCommand.studyId()),
                () -> verify(studyParticipantRepository, times(1))
                        .isApplierOrParticipant(applierCommand.studyId(), applierCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0))
                        .isAlreadyLeaveOrGraduatedParticipant(applierCommand.studyId(), applierCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("스터디 참여를 취소했거나 졸업한 사람은 동일 스터디에 다시 참여 신청을 할 수 없다")
    void throwExceptionByAlreadyLeaveOrGraduated() {
        // given
        given(studyRepository.getRecruitingStudy(applierCommand.studyId())).willReturn(study);
        given(studyParticipantRepository.isApplierOrParticipant(applierCommand.studyId(), applierCommand.applierId()))
                .willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(applierCommand.studyId(), applierCommand.applierId()))
                .willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(applierCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getRecruitingStudy(applierCommand.studyId()),
                () -> verify(studyParticipantRepository, times(1))
                        .isApplierOrParticipant(applierCommand.studyId(), applierCommand.applierId()),
                () -> verify(studyParticipantRepository, times(1))
                        .isAlreadyLeaveOrGraduatedParticipant(applierCommand.studyId(), applierCommand.applierId()),
                () -> verify(studyParticipantRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("스터디에 참여 신청을 한다")
    void success() {
        // given
        given(studyRepository.getRecruitingStudy(applierCommand.studyId())).willReturn(study);
        given(studyParticipantRepository.isApplierOrParticipant(applierCommand.studyId(), applierCommand.applierId()))
                .willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(applierCommand.studyId(), applierCommand.applierId()))
                .willReturn(false);

        final StudyParticipant applier = applierCommand.toDomain().apply(1L);
        given(studyParticipantRepository.save(any())).willReturn(applier);

        // when
        final Long savedApplierId = sut.invoke(applierCommand);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getRecruitingStudy(applierCommand.studyId()),
                () -> verify(studyParticipantRepository, times(1))
                        .isApplierOrParticipant(applierCommand.studyId(), applierCommand.applierId()),
                () -> verify(studyParticipantRepository, times(1))
                        .isAlreadyLeaveOrGraduatedParticipant(applierCommand.studyId(), applierCommand.applierId()),
                () -> verify(studyParticipantRepository, times(1)).save(any()),
                () -> assertThat(savedApplierId).isEqualTo(applier.getId())
        );
    }
}
