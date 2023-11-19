package com.kgu.studywithme.studyparticipant.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipationInspector {
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;

    public void checkApplierIsHost(final Study study, final Member applier) {
        if (study.isHost(applier)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_HOST_CANNOT_APPLY);
        }
    }

    public void checkApplierIsAlreadyRelatedToStudy(final Study study, final Member applier) {
        if (studyParticipantRepository.isApplierOrParticipant(study.getId(), applier.getId())) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.ALREADY_APPLY_OR_PARTICIPATE);
        }

        if (studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), applier.getId())) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.ALREADY_LEAVE_OR_GRADUATED);
        }
    }

    public void checkNewHostIsCurrentHost(final Study study, final Member participant) {
        if (study.isHost(participant)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.SELF_DELEGATING_NOT_ALLOWED);
        }
    }

    public void checkLeavingParticipantIsHost(final Study study, final Member participant) {
        if (study.isHost(participant)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY);
        }
    }

    public void checkGraduationCandidateIsHost(final Study study, final Member participant) {
        if (study.isHost(participant)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY);
        }
    }

    public void checkGraduationCandidateMeetGraduationPolicy(final Study study, final Member participant) {
        final int attendanceStatusCount = studyAttendanceRepository.getAttendanceStatusCount(study.getId(), participant.getId());

        if (!study.isParticipantMeetGraduationPolicy(attendanceStatusCount)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY);
        }
    }
}
