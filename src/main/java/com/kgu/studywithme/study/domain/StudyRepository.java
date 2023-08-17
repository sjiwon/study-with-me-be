package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.study.infrastructure.query.StudyCategoryQueryRepository;
import com.kgu.studywithme.study.infrastructure.query.StudyDuplicateCheckRepository;
import com.kgu.studywithme.study.infrastructure.query.StudyInformationQueryRepository;
import com.kgu.studywithme.study.infrastructure.query.StudyVerificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends
        JpaRepository<Study, Long>,
        StudyVerificationRepository,
        StudyDuplicateCheckRepository,
        StudyInformationQueryRepository,
        StudyCategoryQueryRepository {
}
