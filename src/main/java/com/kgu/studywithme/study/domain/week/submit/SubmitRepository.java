package com.kgu.studywithme.study.domain.week.submit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubmitRepository extends JpaRepository<Submit, Long> {
    @Query("SELECT s" +
            " FROM Submit s" +
            " INNER JOIN s.week w" +
            " WHERE s.participant.id = :participantId AND w.week = :week")
    Optional<Submit> findByParticipantIdAndWeek(@Param("participantId") Long participantId,
                                                @Param("week") Integer week);
}
