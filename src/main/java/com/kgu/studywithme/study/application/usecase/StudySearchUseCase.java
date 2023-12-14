package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.global.cache.CacheKeyName;
import com.kgu.studywithme.study.application.usecase.dto.StudyPagingResponse;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByCategory;
import com.kgu.studywithme.study.application.usecase.query.GetStudiesByRecommend;
import com.kgu.studywithme.study.domain.repository.query.StudyCategorySearchRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kgu.studywithme.study.utils.search.PagingConstants.createPageRequest;

@Service
@RequiredArgsConstructor
public class StudySearchUseCase {
    private final StudyCategorySearchRepository studyCategorySearchRepository;

    @Cacheable(
            value = CacheKeyName.STUDY_SEARCH,
            key = """
                    @cacheKeyGenerator.createStudyKey(
                        #query.condition().category(),
                        #query.condition().sort(),
                        #query.condition().type(),
                        #query.condition().province(),
                        #query.condition().city(),
                        #query.page()
                    )
                    """,
            cacheManager = "studySearchCacheManager",
            unless = "#result.studies().isEmpty()"
    )
    public StudyPagingResponse getStudiesByCategory(final GetStudiesByCategory query) {
        final List<StudyPreview> result = studyCategorySearchRepository.fetchStudyByCategory(query.condition(), createPageRequest(query.page()));
        return new StudyPagingResponse(result);
    }

    @Cacheable(
            value = CacheKeyName.STUDY_SEARCH,
            key = """
                    @cacheKeyGenerator.createStudyKey(
                        #query.condition().memberId(),
                        #query.condition().sort(),
                        #query.condition().type(),
                        #query.condition().province(),
                        #query.condition().city(),
                        #query.page()
                    )
                    """,
            cacheManager = "studySearchCacheManager",
            unless = "#result.studies().isEmpty()"
    )
    public StudyPagingResponse getStudiesByRecommend(final GetStudiesByRecommend query) {
        final List<StudyPreview> result = studyCategorySearchRepository.fetchStudyByRecommend(query.condition(), createPageRequest(query.page()));
        return new StudyPagingResponse(result);
    }
}
