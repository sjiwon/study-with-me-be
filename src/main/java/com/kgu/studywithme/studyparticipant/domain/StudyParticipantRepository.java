package com.kgu.studywithme.studyparticipant.domain;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {
    @Query("SELECT sp" +
            " FROM StudyParticipant sp" +
            " WHERE sp.studyId = :studyId AND sp.memberId = :memberId AND sp.status = :status")
    Optional<StudyParticipant> findParticipantByStatus(
            @Param("studyId") final Long studyId,
            @Param("memberId") final Long memberId,
            @Param("status") final ParticipantStatus status
    );

    @Query("SELECT sp.memberId" +
            " FROM StudyParticipant sp" +
            " WHERE sp.studyId = :studyId AND sp.status = :status")
    List<Long> findParticipantIdsByStatus(
            @Param("studyId") final Long studyId,
            @Param("status") final ParticipantStatus status
    );

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE StudyParticipant sp" +
            " SET sp.status = :status" +
            " WHERE sp.studyId = :studyId AND sp.memberId = :memberId")
    void updateParticipantStatus(
            @Param("studyId") final Long studyId,
            @Param("memberId") final Long memberId,
            @Param("status") final ParticipantStatus status
    );
}
