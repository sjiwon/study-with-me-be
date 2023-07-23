package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.QueryStudyByCategoryCondition;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudyCategoryQueryRepository {
    Slice<StudyPreview> fetchStudyByCategory(final QueryStudyByCategoryCondition condition, final Pageable pageable);

    Slice<StudyPreview> fetchStudyByRecommend(final QueryStudyByRecommendCondition condition, final Pageable pageable);
}
