package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Member m" +
            " SET m.score.value = m.score.value - 5" +
            " WHERE m.id IN :absenceParticipantIds")
    void applyScoreToAbsenceParticipant(@Param("absenceParticipantIds") Set<Long> absenceParticipantIds);

    @Query("SELECT m" +
            " FROM Member m" +
            " WHERE m.email.value = :email")
    Optional<Member> findByEmail(@Param("email") String email);
}
