package com.kgu.studywithme.studyattendance.application.adapter;

import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.StudyAttendanceWeekly;

import java.util.List;

public interface StudyAttendanceHandlingRepositoryAdapter {
    List<NonAttendanceWeekly> findNonAttendanceInformation();

    List<StudyAttendanceWeekly> findParticipateWeeksInStudyByMemberId(final Long memberId);

    int getAttendanceCount(final Long studyId, final Long participantId);
}
