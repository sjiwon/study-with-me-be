package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.application.dto.StudyPagingResponse;
import com.kgu.studywithme.study.utils.QueryStudyByCategoryCondition;
import org.springframework.data.domain.Pageable;

public interface QueryStudyByCategoryUseCase {
    StudyPagingResponse invoke(final Query query);

    record Query(
            QueryStudyByCategoryCondition condition,
            Pageable pageable
    ) {
    }
}
