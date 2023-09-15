package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.study.domain.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
