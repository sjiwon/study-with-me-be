package com.kgu.studywithme.studyattendance.domain.repository.query;

import com.kgu.studywithme.studyattendance.domain.repository.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;

import java.util.List;

public interface StudyAttendanceMetadataRepository {
    List<NonAttendanceWeekly> findParticipantNonAttendanceWeekly();

    List<StudyAttendanceWeekly> findMemberParticipateWeekly(final Long memberId);
}
