package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipateMemberReadAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class GraduateStudyService implements GraduateStudyUseCase {
    private final StudyRepository studyRepository;
    private final ParticipateMemberReadAdapter participateMemberReadAdapter;
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        final Study study = studyRepository.getById(command.studyId());
        validateMemberIsHost(study, command.participantId());

        final Member participant = participateMemberReadAdapter.getParticipant(command.studyId(), command.participantId());
        validateParticipantMeetGraduationPolicy(study, participant);

        graduateStudy(study, participant);
        if (participant.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyGraduatedEvent(
                            participant.getEmail().getValue(),
                            participant.getNickname().getValue(),
                            study.getName().getValue()
                    )
            );
        }
    }

    private void validateMemberIsHost(final Study study, final Long participantId) {
        if (study.isHost(participantId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY);
        }
    }

    private void validateParticipantMeetGraduationPolicy(final Study study, final Member participant) {
        final int attendanceCount = getAttendanceCount(study.getId(), participant.getId());

        if (!study.isParticipantMeetGraduationPolicy(attendanceCount)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY);
        }
    }

    private int getAttendanceCount(final Long studyId, final Long participantId) {
        return studyAttendanceRepository.countByStudyIdAndParticipantIdAndStatus(studyId, participantId, ATTENDANCE);
    }

    private void graduateStudy(final Study study, final Member participant) {
        study.removeParticipant();
        studyParticipantRepository.updateParticipantStatus(study.getId(), participant.getId(), GRADUATED);
    }
}
