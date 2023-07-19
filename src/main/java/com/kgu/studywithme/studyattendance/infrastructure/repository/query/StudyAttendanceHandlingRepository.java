package com.kgu.studywithme.studyattendance.infrastructure.repository.query;

import com.kgu.studywithme.studyattendance.domain.StudyAttendance;

import java.util.Optional;

public interface StudyAttendanceHandlingRepository {
    Optional<StudyAttendance> getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final Integer week
    );

    int getAttendanceCount(final Long studyId, final Long participantId);
}
