package com.kgu.studywithme.studyattendance.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.exception.StudyAttendanceErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;

public interface StudyAttendanceRepository extends JpaRepository<StudyAttendance, Long> {
    // @Query
    @Query("SELECT sa" +
            " FROM StudyAttendance sa" +
            " WHERE sa.study.id = :studyId AND sa.participant.id = :participantId AND sa.week = :week")
    Optional<StudyAttendance> findParticipantAttendanceByWeek(
            @Param("studyId") final Long studyId,
            @Param("participantId") final Long participantId,
            @Param("week") final Integer week
    );

    default StudyAttendance getParticipantAttendanceByWeek(final Long studyId, final Long participantId, final int week) {
        return findParticipantAttendanceByWeek(studyId, participantId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyAttendanceErrorCode.ATTENDANCE_NOT_FOUND));
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE StudyAttendance st" +
            " SET st.status = :status" +
            " WHERE st.study.id = :studyId AND st.participant.id IN :participantIds AND st.week = :week")
    void updateParticipantStatus(
            @Param("studyId") final Long studyId,
            @Param("week") final int week,
            @Param("participantIds") final Set<Long> participantIds,
            @Param("status") final AttendanceStatus status
    );

    @Query("SELECT sa" +
            " FROM StudyAttendance sa" +
            " JOIN FETCH sa.study" +
            " JOIN FETCH sa.participant" +
            " WHERE sa.status = :status" +
            " ORDER BY sa.study.id ASC, sa.participant.id ASC")
    List<StudyAttendance> findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(@Param("status") final AttendanceStatus status);

    default List<StudyAttendance> findNonAttendanceInformation() {
        return findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(NON_ATTENDANCE);
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM StudyAttendance sa WHERE sa.study.id = :studyId AND sa.week = :week")
    int deleteFromSpecificWeekly(@Param("studyId") final Long studyId, @Param("week") final int week);

    // Query Method
    int countByStudyIdAndParticipantIdAndStatus(final Long studyId, final Long participantId, final AttendanceStatus status);

    default int getAttendanceStatusCount(final Long studyId, final Long participantId) {
        return countByStudyIdAndParticipantIdAndStatus(studyId, participantId, ATTENDANCE);
    }

    default int getLateStatusCount(final Long studyId, final Long participantId) {
        return countByStudyIdAndParticipantIdAndStatus(studyId, participantId, LATE);
    }

    default int getAbsenceStatusCount(final Long studyId, final Long participantId) {
        return countByStudyIdAndParticipantIdAndStatus(studyId, participantId, ABSENCE);
    }

    default int getNonAttendanceStatusCount(final Long studyId, final Long participantId) {
        return countByStudyIdAndParticipantIdAndStatus(studyId, participantId, NON_ATTENDANCE);
    }

    boolean existsByStudyIdAndWeek(final Long studyId, final int week);

    int countByStudyIdAndWeek(final Long studyId, final int week);
}
