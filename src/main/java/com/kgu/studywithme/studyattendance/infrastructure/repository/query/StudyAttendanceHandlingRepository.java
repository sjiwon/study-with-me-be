package com.kgu.studywithme.studyattendance.infrastructure.repository.query;

import com.kgu.studywithme.studyattendance.domain.StudyAttendance;

import java.util.Optional;

public interface StudyAttendanceHandlingRepository {
    Optional<StudyAttendance> getParticipantAttendanceByWeek(
            final Long studyId,
            final Long participantId,
            final Integer week
    );

//    void updateParticipantStatus(
//            final Long studyId,
//            final int week,
//            final Set<Long> participantIds,
//            final AttendanceStatus attendanceStatus
//    );
    // TODO QueryDsl Bug -> jOOQ Migration
}
