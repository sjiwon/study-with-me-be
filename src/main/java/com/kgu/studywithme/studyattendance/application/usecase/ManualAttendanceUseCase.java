package com.kgu.studywithme.studyattendance.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceCommand;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.domain.service.ParticipantScoreUpdateProcessor;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ManualAttendanceUseCase {
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final ParticipantScoreUpdateProcessor participantScoreUpdateProcessor;

    public void invoke(final ManualAttendanceCommand command) {
        final StudyAttendance attendance = getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week());
        final AttendanceStatus previousStatus = attendance.getStatus();
        final AttendanceStatus currentStatus = command.attendanceStatus();

        attendance.updateAttendanceStatus(currentStatus);
        participantScoreUpdateProcessor.updateByAttendanceStatus(command.participantId(), previousStatus, currentStatus);
    }

    private StudyAttendance getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final Integer week
    ) {
        return studyAttendanceRepository.findParticipantAttendanceByWeek(studyId, participantId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyAttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    }
}
