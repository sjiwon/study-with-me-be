package com.kgu.studywithme.studyattendance.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceCommand;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;

@Service
@RequiredArgsConstructor
public class ManualAttendanceUseCase {
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final ParticipateMemberReader participateMemberReader;

    @StudyWithMeWritableTransactional
    public void invoke(final ManualAttendanceCommand command) {
        final Member participant = participateMemberReader.getParticipant(command.studyId(), command.participantId());
        final StudyAttendance attendance = studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week());
        final AttendanceStatus previousStatus = attendance.getStatus();
        final AttendanceStatus currentStatus = command.attendanceStatus();

        attendance.updateAttendanceStatus(currentStatus);
        updateParticipantScoreByAttendanceStatus(participant, previousStatus, currentStatus);
    }

    private void updateParticipantScoreByAttendanceStatus(
            final Member participant,
            final AttendanceStatus previousStatus,
            final AttendanceStatus currentStatus
    ) {
        if (previousStatus == NON_ATTENDANCE) {
            participant.applyScoreByAttendanceStatus(currentStatus);
        } else if (isStatusChanged(previousStatus, currentStatus)) {
            participant.applyScoreByAttendanceStatus(previousStatus, currentStatus);
        }
    }

    private boolean isStatusChanged(final AttendanceStatus previousStatus, final AttendanceStatus currentStatus) {
        return previousStatus != currentStatus;
    }
}
