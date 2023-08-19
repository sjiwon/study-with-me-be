package com.kgu.studywithme.studyparticipant.infrastructure.persistence;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyparticipant.domain.ParticipantStatus;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyParticipantJpaRepository extends JpaRepository<StudyParticipant, Long> {
    @Query("SELECT sp" +
            " FROM StudyParticipant sp" +
            " WHERE sp.studyId = :studyId AND sp.memberId = :memberId AND sp.status = 'APPLY'")
    Optional<StudyParticipant> findApplier(@Param("studyId") final Long studyId, @Param("memberId") final Long memberId);

    @Query("SELECT sp.memberId" +
            " FROM StudyParticipant sp" +
            " WHERE sp.studyId = :studyId AND sp.status = 'APPROVE'")
    List<Long> findStudyParticipantIds(@Param("studyId") final Long studyId);

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