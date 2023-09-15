package com.kgu.studywithme.auth.domain.repository;

import com.kgu.studywithme.auth.domain.model.Token;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // @Query
    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t" +
            " SET t.refreshToken = :refreshToken" +
            " WHERE t.memberId = :memberId")
    void updateMemberRefreshToken(@Param("memberId") Long memberId, @Param("refreshToken") String newRefreshToken);

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Token t WHERE t.memberId = :memberId")
    void deleteMemberRefreshToken(@Param("memberId") Long memberId);

    // Query Method
    Optional<Token> findByMemberId(Long memberId);

    boolean existsByMemberIdAndRefreshToken(Long memberId, String refreshToken);
}
