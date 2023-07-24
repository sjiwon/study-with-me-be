package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.application.dto.StudyPagingResponse;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
import org.springframework.data.domain.Pageable;

public interface QueryStudyByRecommendUseCase {
    StudyPagingResponse queryStudyByRecommend(final Query query);

    record Query(
            QueryStudyByRecommendCondition condition,
            Pageable pageable
    ) {
    }
}