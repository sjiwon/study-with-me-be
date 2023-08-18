package com.kgu.studywithme.study.infrastructure.persistence.hashtag;

import com.kgu.studywithme.study.domain.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagJpaRepository extends JpaRepository<Hashtag, Long> {
}
