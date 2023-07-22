package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.study.infrastructure.repository.query.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends
        JpaRepository<Study, Long>,
        StudyVerificationRepository,
        StudyDuplicateCheckRepository,
        StudySimpleQueryRepository,
        StudyCategoryQueryRepository,
        StudyInformationQueryRepository {
}
