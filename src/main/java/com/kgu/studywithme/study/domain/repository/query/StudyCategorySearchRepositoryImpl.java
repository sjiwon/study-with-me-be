package com.kgu.studywithme.study.domain.repository.query;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.domain.model.StudyType;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyPreview;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyPreview_FavoriteSummary;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyPreview_HashtagSummary;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;
import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;
import com.kgu.studywithme.study.utils.search.SearchSortType;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.model.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.model.QInterest.interest;
import static com.kgu.studywithme.study.domain.model.QHashtag.hashtag;
import static com.kgu.studywithme.study.domain.model.QStudy.study;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyCategorySearchRepositoryImpl implements StudyCategorySearchRepository {
    private final JPAQueryFactory query;

    @Override
    public List<StudyPreview> fetchStudyByCategory(
            final SearchByCategoryCondition condition,
            final Pageable pageable
    ) {
        return projectionStudyPreview(
                condition.sort(),
                pageable,
                Arrays.asList(
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyTypeEq(condition.type()),
                        studyCategoryEq(condition.category()),
                        studyIsNotTerminated()
                )
        );
    }

    @Override
    public List<StudyPreview> fetchStudyByRecommend(
            final SearchByRecommendCondition condition,
            final Pageable pageable
    ) {
        final List<Category> memberInterests = query
                .select(interest.category)
                .from(interest)
                .where(interest.member.id.eq(condition.memberId()))
                .fetch();

        return projectionStudyPreview(
                condition.sort(),
                pageable,
                Arrays.asList(
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyTypeEq(condition.type()),
                        studyCategoryIn(memberInterests),
                        studyIsNotTerminated()
                )
        );
    }

    private List<StudyPreview> projectionStudyPreview(
            final SearchSortType sortType,
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        return switch (sortType) {
            case DATE -> projectionByDate(pageable, whereConditions);
            case FAVORITE -> projectionByFavoriteCount(pageable, whereConditions);
            default -> projectionByReviewCount(pageable, whereConditions);
        };
    }

    private List<StudyPreview> projectionByDate(
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final List<StudyPreview> result = query
                .select(studyPreviewProjection())
                .from(study)
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(study.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (result.isEmpty()) {
            return List.of();
        }

        final List<Long> studyIds = result.stream()
                .map(StudyPreview::getId)
                .toList();

        applyStudyHashtags(result, studyIds);
        applyLikeMarkingMembers(result, studyIds);
        return result;
    }

    private List<StudyPreview> projectionByFavoriteCount(
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final List<StudyPreview> result = query
                .select(studyPreviewProjection())
                .from(study)
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(study.favoriteCount.desc(), study.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (result.isEmpty()) {
            return List.of();
        }

        final List<Long> studyIds = result.stream()
                .map(StudyPreview::getId)
                .toList();

        applyStudyHashtags(result, studyIds);
        applyLikeMarkingMembers(result, studyIds);
        return result;
    }

    private List<StudyPreview> projectionByReviewCount(
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final List<StudyPreview> result = query
                .select(studyPreviewProjection())
                .from(study)
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(study.reviewCount.desc(), study.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (result.isEmpty()) {
            return List.of();
        }

        final List<Long> studyIds = result.stream()
                .map(StudyPreview::getId)
                .toList();

        applyStudyHashtags(result, studyIds);
        applyLikeMarkingMembers(result, studyIds);
        return result;
    }

    private ConstructorExpression<StudyPreview> studyPreviewProjection() {
        return new QStudyPreview(
                study.id,
                study.name,
                study.description,
                study.category,
                study.thumbnail,
                study.type,
                study.recruitmentStatus,
                study.capacity,
                study.participants,
                study.createdAt
        );
    }

    private void applyStudyHashtags(
            final List<StudyPreview> result,
            final List<Long> studyIds
    ) {
        final List<StudyPreview.HashtagSummary> hashtags = query
                .select(new QStudyPreview_HashtagSummary(
                        hashtag.study.id,
                        hashtag.name
                ))
                .from(hashtag)
                .where(hashtag.study.id.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyHashtags(collectHashtags(study, hashtags)));
    }

    private List<String> collectHashtags(
            final StudyPreview study,
            final List<StudyPreview.HashtagSummary> hashtags
    ) {
        return hashtags
                .stream()
                .filter(hashtag -> hashtag.studyId().equals(study.getId()))
                .map(StudyPreview.HashtagSummary::value)
                .toList();
    }

    private void applyLikeMarkingMembers(
            final List<StudyPreview> result,
            final List<Long> studyIds
    ) {
        final List<StudyPreview.FavoriteSummary> favorites = query
                .select(new QStudyPreview_FavoriteSummary(
                        favorite.study.id,
                        favorite.member.id
                ))
                .from(favorite)
                .where(favorite.study.id.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyLikeMarkingMembers(collectLikeMarkingMembers(study, favorites)));
    }

    private List<Long> collectLikeMarkingMembers(
            final StudyPreview study,
            final List<StudyPreview.FavoriteSummary> favorites
    ) {
        return favorites
                .stream()
                .filter(favorite -> favorite.studyId().equals(study.getId()))
                .map(StudyPreview.FavoriteSummary::memberId)
                .toList();
    }

    private BooleanExpression studyLocationProvinceEq(final String province) {
        return (province != null) ? study.location.province.eq(province) : null;
    }

    private BooleanExpression studyLocationCityEq(final String city) {
        return (city != null) ? study.location.city.eq(city) : null;
    }

    private BooleanExpression studyTypeEq(final String type) {
        return (type != null) ? study.type.eq(StudyType.from(type)) : null;
    }

    private BooleanExpression studyCategoryEq(final Category category) {
        return study.category.eq(category);
    }

    private BooleanExpression studyCategoryIn(final List<Category> categories) {
        return study.category.in(categories);
    }

    private BooleanExpression studyIsNotTerminated() {
        return study.terminated.isFalse();
    }

    private boolean hasNext(
            final Pageable pageable,
            final int contentSize,
            final Long totalCount
    ) {
        if (contentSize == pageable.getPageSize()) {
            return (long) contentSize * (pageable.getPageNumber() + 1) != totalCount;
        }
        return false;
    }
}
