package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;

import java.util.List;

public interface MemberAttendanceRepository {
    List<AttendanceRatio> findAttendanceRatioByMemberId(Long memberId);

    List<StudyParticipateWeeks> findParticipateWeeksInStudyByMemberId(Long memberId);
}
