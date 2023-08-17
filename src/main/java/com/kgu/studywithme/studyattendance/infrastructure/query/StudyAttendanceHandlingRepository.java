package com.kgu.studywithme.studyattendance.infrastructure.query;

import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;

import java.util.List;
import java.util.Optional;

public interface StudyAttendanceHandlingRepository {
    Optional<StudyAttendance> getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final Integer week
    );

    int getAttendanceCount(final Long studyId, final Long participantId);

    List<NonAttendanceWeekly> findNonAttendanceInformation();
}
