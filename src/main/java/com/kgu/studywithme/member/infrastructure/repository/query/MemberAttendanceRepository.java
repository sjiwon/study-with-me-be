package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;

import java.util.List;

public interface MemberAttendanceRepository {
    List<StudyParticipateWeeks> findParticipateWeeksInStudyByMemberId(Long memberId);
}
