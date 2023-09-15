package com.kgu.studywithme.member.domain.repository;

import com.kgu.studywithme.member.domain.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
}
