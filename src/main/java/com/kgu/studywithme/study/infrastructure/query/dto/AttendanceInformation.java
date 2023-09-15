package com.kgu.studywithme.study.infrastructure.query.dto;

import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record AttendanceInformation(
        StudyMember member,
        List<AttendanceSummary> summaries
) {
    public record AttendanceParticipant(
            StudyMember participant,
            int week,
            String attendanceStatus
    ) {
        @QueryProjection
        public AttendanceParticipant(
                final Long id,
                final Nickname nickname,
                final int week,
                final AttendanceStatus attendanceStatus
        ) {
            this(
                    new StudyMember(id, nickname.getValue()),
                    week,
                    attendanceStatus.getValue()
            );
        }
    }

    public record AttendanceSummary(
            int week,
            String attendanceStatus
    ) {
    }
}
