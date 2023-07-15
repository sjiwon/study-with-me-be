package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.member.infrastructure.repository.query.MemberDuplicateCheckQueryRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.MemberReportHandlingQueryRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.MemberSimpleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends
        JpaRepository<Member, Long>,
        MemberDuplicateCheckQueryRepository,
        MemberReportHandlingQueryRepository,
        MemberSimpleQueryRepository {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Member m" +
            " SET m.score.value = m.score.value - 5" +
            " WHERE m.id IN :absenceParticipantIds")
    void applyAbsenceScore(@Param("absenceParticipantIds") Set<Long> absenceParticipantIds);

    // Query Method
    Optional<Member> findByEmail(Email email);
}
