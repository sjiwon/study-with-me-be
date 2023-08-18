package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.application.service.dto.StudyPagingResponse;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
import org.springframework.data.domain.Pageable;

public interface QueryStudyByRecommendUseCase {
    StudyPagingResponse invoke(final Query query);

    record Query(
            QueryStudyByRecommendCondition condition,
            Pageable pageable
    ) {
    }
}
