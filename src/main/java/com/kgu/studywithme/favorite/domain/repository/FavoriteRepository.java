package com.kgu.studywithme.favorite.domain.repository;

import com.kgu.studywithme.favorite.domain.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // @Query
    Optional<Favorite> findByStudyIdAndMemberId(final Long studyId, final Long memberId);
}
