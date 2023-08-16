package com.kgu.studywithme.member.application.adapter;

import com.kgu.studywithme.member.infrastructure.query.dto.StudyParticipateWeeks;

import java.util.List;

public interface MemberAttendanceRepository {
    List<StudyParticipateWeeks> findParticipateWeeksInStudyByMemberId(Long memberId);
}
