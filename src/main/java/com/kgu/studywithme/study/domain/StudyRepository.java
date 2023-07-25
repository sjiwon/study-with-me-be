package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.study.infrastructure.repository.query.StudyCategoryQueryRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.StudyDuplicateCheckRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.StudyInformationQueryRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.StudyVerificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends
        JpaRepository<Study, Long>,
        StudyVerificationRepository,
        StudyDuplicateCheckRepository,
        StudyInformationQueryRepository,
        StudyCategoryQueryRepository {
}
