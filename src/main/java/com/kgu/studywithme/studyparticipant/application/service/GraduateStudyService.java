package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.service.QueryStudyByIdService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class GraduateStudyService implements GraduateStudyUseCase {
    private final QueryStudyByIdService queryStudyByIdService;
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void graduateStudy(final Command command) {
        final Study study = queryStudyByIdService.findById(command.studyId());
        validateMemberIsHost(study, command.participantId());

        final Member participant = getParticipant(command.studyId(), command.participantId());
        validateParticipantMeetGraduationPolicy(study, participant);

        studyParticipantRepository.updateParticipantStatus(command.studyId(), command.participantId(), GRADUATED);
        study.removeParticipant();

        if (participant.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyGraduatedEvent(
                            participant.getEmailValue(),
                            participant.getNicknameValue(),
                            study.getNameValue()
                    )
            );
        }
    }

    private void validateMemberIsHost(final Study study, final Long participantId) {
        if (study.isHost(participantId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY);
        }
    }

    private Member getParticipant(final Long studyId, final Long participantId) {
        return studyParticipantRepository.findParticipant(studyId, participantId)
                .orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND));
    }

    private void validateParticipantMeetGraduationPolicy(final Study study, final Member participant) {
        final int attendanceCount = studyAttendanceRepository.getAttendanceCount(study.getId(), participant.getId());

        if (!study.isParticipantMeetGraduationPolicy(attendanceCount)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY);
        }
    }
}
