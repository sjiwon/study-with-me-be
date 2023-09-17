package com.kgu.studywithme.studyattendance.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface StudyAttendanceRepository extends JpaRepository<StudyAttendance, Long> {
    // @Query
    @Query("SELECT sa" +
            " FROM StudyAttendance sa" +
            " WHERE sa.studyId = :studyId AND sa.participantId = :participantId AND sa.week = :week")
    Optional<StudyAttendance> getParticipantAttendanceByWeek(
            @Param("studyId") final Long studyId,
            @Param("participantId") final Long participantId,
            @Param("week") final Integer week
    );

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE StudyAttendance st" +
            " SET st.status = :status" +
            " WHERE st.studyId = :studyId AND st.week = :week AND st.participantId IN :participantIds")
    void updateParticipantStatus(
            @Param("studyId") Long studyId,
            @Param("week") int week,
            @Param("participantIds") Set<Long> participantIds,
            @Param("status") AttendanceStatus status
    );

    // Method Query
    int countByStudyIdAndParticipantIdAndStatus(final Long studyId, final Long participantId, final AttendanceStatus status);
}
