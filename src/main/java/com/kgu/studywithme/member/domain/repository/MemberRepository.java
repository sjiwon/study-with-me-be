package com.kgu.studywithme.member.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {
    default Member getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // @Query
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

    @Query("SELECT m.id" +
            " FROM Member m" +
            " WHERE m.nickname.value = :nickname")
    List<Long> findIdByNicknameUsed(@Param("nickname") final String nickname);

    @Query("SELECT m.id" +
            " FROM Member m" +
            " WHERE m.phone.value = :phone")
    List<Long> findIdByPhoneUsed(@Param("phone") final String phone);

    // Method Query
    boolean existsByEmailValue(final String email);

    boolean existsByNicknameValue(final String nickname);

    boolean existsByPhoneValue(final String phone);
}
