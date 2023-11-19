package com.kgu.studywithme.studyparticipant.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("StudyParticipant -> ParticipationInspector 테스트")
public class ParticipationInspectorTest extends ParallelTest {
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final ParticipationInspector sut = new ParticipationInspector(studyParticipantRepository, studyAttendanceRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member applier = GHOST.toMember().apply(2L);
    private final Member participantA = DUMMY1.toMember().apply(3L);
    private final Member participantB = DUMMY2.toMember().apply(4L);
    private final Member leaveMember = DUMMY3.toMember().apply(5L);
    private final Member graduateMember = DUMMY4.toMember().apply(6L);
    private final Member anonymous = ANONYMOUS.toMember().apply(7L);
    private final Study study = SPRING.toStudy(host).apply(1L);

    @Test
    @DisplayName("신청자가 스터디 팀장인지 확인한다")
    void checkApplierIsHost() {
        assertAll(
                () -> assertDoesNotThrow(() -> sut.checkApplierIsHost(study, applier)),
                () -> assertDoesNotThrow(() -> sut.checkApplierIsHost(study, anonymous)),
                () -> assertThatThrownBy(() -> sut.checkApplierIsHost(study, host))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY.getMessage())
        );
    }

    @Test
    @DisplayName("신청자가 이미 스터디와 관련있는 사람인지 확인한다 [이미 신청 | 참여중 | 떠남 | 졸업]")
    void checkApplierIsAlreadyRelatedToStudy() {
        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), host.getId())).willReturn(true);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), host.getId())).willReturn(false);
        assertThatThrownBy(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, host))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE.getMessage());

        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), applier.getId())).willReturn(true);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), applier.getId())).willReturn(false);
        assertThatThrownBy(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, applier))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE.getMessage());

        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), participantA.getId())).willReturn(true);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), participantA.getId())).willReturn(false);
        assertThatThrownBy(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, participantA))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE.getMessage());

        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), participantB.getId())).willReturn(true);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), participantB.getId())).willReturn(false);
        assertThatThrownBy(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, participantB))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE.getMessage());

        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), leaveMember.getId())).willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), leaveMember.getId())).willReturn(true);
        assertThatThrownBy(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, leaveMember))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED.getMessage());

        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), graduateMember.getId())).willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), graduateMember.getId())).willReturn(true);
        assertThatThrownBy(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, graduateMember))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED.getMessage());

        given(studyParticipantRepository.isApplierOrParticipant(study.getId(), anonymous.getId())).willReturn(false);
        given(studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), anonymous.getId())).willReturn(false);
        assertDoesNotThrow(() -> sut.checkApplierIsAlreadyRelatedToStudy(study, anonymous));
    }

    @Test
    @DisplayName("새로운 스터디 팀장 후보가 현재 팀장인지 확인한다")
    void checkNewHostIsCurrentHost() {
        assertAll(
                () -> assertDoesNotThrow(() -> sut.checkNewHostIsCurrentHost(study, participantA)),
                () -> assertDoesNotThrow(() -> sut.checkNewHostIsCurrentHost(study, participantB)),
                () -> assertThatThrownBy(() -> sut.checkNewHostIsCurrentHost(study, host))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.SELF_DELEGATING_NOT_ALLOWED.getMessage())
        );
    }

    @Test
    @DisplayName("스터디를 떠나려는 참여자가 팀장인지 확인한다")
    void checkLeavingParticipantIsHost() {
        assertAll(
                () -> assertDoesNotThrow(() -> sut.checkLeavingParticipantIsHost(study, participantA)),
                () -> assertDoesNotThrow(() -> sut.checkLeavingParticipantIsHost(study, participantB)),
                () -> assertThatThrownBy(() -> sut.checkLeavingParticipantIsHost(study, host))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY.getMessage())
        );
    }

    @Test
    @DisplayName("스터디를 졸업하려는 참여자가 팀장인지 확인한다")
    void checkGraduationCandidateIsHost() {
        assertAll(
                () -> assertDoesNotThrow(() -> sut.checkGraduationCandidateIsHost(study, participantA)),
                () -> assertDoesNotThrow(() -> sut.checkGraduationCandidateIsHost(study, participantB)),
                () -> assertThatThrownBy(() -> sut.checkGraduationCandidateIsHost(study, host))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY.getMessage())
        );
    }

    @Test
    @DisplayName("스터디 졸업 후보자가 졸업 요건을 만족했는지 확인한다")
    void checkGraduationCandidateMeetGraduationPolicy() {
        given(studyAttendanceRepository.getAttendanceStatusCount(study.getId(), participantA.getId()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance());
        given(studyAttendanceRepository.getAttendanceStatusCount(study.getId(), participantB.getId()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance() - 1);

        assertAll(
                () -> assertDoesNotThrow(() -> sut.checkGraduationCandidateMeetGraduationPolicy(study, participantA)),
                () -> assertThatThrownBy(() -> sut.checkGraduationCandidateMeetGraduationPolicy(study, participantB))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY.getMessage())
        );
    }
}
