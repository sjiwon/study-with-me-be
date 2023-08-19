package com.kgu.studywithme.studyattendance.infrastructure.persistence;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface StudyAttendanceJpaRepository extends JpaRepository<StudyAttendance, Long> {
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

    @Query("SELECT COUNT(st.id)" +
            " FROM StudyAttendance st" +
            " WHERE st.studyId = :studyId AND st.participantId = :participantId AND st.status = 'ATTENDANCE'")
    int getAttendanceCount(
            @Param("studyId") final Long studyId,
            @Param("participantId") final Long participantId
    );
}
