package com.kgu.studywithme.study.infrastructure.query.dto;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

public record AttendanceInformation(
        StudyMember member,
        List<AttendanceSummary> summaries
) {
    public record AttenadnceParticipant(
            StudyMember participant,
            int week,
            String attendanceStatus
    ) {
        @QueryProjection
        public AttenadnceParticipant(
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
