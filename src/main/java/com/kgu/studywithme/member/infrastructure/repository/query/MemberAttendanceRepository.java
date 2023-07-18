package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;

import java.util.List;

public interface MemberAttendanceRepository {
    List<AttendanceRatio> findAttendanceRatioByMemberId(Long memberId);

    List<StudyParticipateWeeks> findParticipateWeeksInStudyByMemberId(Long memberId);

    Long getAttendanceCount(Long studyId, Long memberId, AttendanceStatus status);
}
