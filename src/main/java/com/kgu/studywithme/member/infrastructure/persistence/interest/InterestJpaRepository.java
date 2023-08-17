package com.kgu.studywithme.member.infrastructure.persistence.interest;

import com.kgu.studywithme.member.domain.interest.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestJpaRepository extends JpaRepository<Interest, Long> {
}
