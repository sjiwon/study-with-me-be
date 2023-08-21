package com.kgu.studywithme.favorite.infrastructure.persistence;

import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteJpaRepository extends JpaRepository<Favorite, Long> {
    // @Query
    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Favorite f WHERE f.memberId = :memberId AND f.studyId = :studyId")
    void cancelLikeMarking(
            @Param("memberId") Long memberId,
            @Param("studyId") Long studyId
    );
}
