package com.kgu.studywithme.member.infrastructure.repository.query;


import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyAttendanceMetadata;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;

import java.util.List;

public interface MemberSimpleQueryRepository {
    List<StudyAttendanceMetadata> findStudyAttendanceMetadataByMemberId(Long memberId);

    Long getAttendanceCount(Long studyId, Long memberId, AttendanceStatus status);

    List<AttendanceRatio> findAttendanceRatioByMemberId(Long memberId);
}
