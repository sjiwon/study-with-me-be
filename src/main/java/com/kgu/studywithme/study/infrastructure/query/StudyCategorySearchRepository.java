package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.application.adapter.StudyCategorySearchRepositoryAdapter;
import com.kgu.studywithme.study.domain.StudyType;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyPreview;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyPreview_HashtagSummary;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.PagingConstants.SortType;
import com.kgu.studywithme.study.utils.QueryStudyByCategoryCondition;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.interest.QInterest.interest;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;
import static com.kgu.studywithme.studyreview.domain.QStudyReview.studyReview;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyCategorySearchRepository implements StudyCategorySearchRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public Slice<StudyPreview> fetchStudyByCategory(
            final QueryStudyByCategoryCondition condition,
            final Pageable pageable
    ) {
        final List<StudyPreview> result = projectionStudyPreview(
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

        final Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyTypeEq(condition.type()),
                        studyCategoryEq(condition.category()),
                        studyIsNotTerminated()
                )
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                hasNext(pageable, result.size(), totalCount)
        );
    }

    @Override
    public Slice<StudyPreview> fetchStudyByRecommend(
            final QueryStudyByRecommendCondition condition,
            final Pageable pageable
    ) {
        final List<Category> memberInterests = query
                .select(interest.category)
                .from(interest)
                .where(interest.member.id.eq(condition.memberId()))
                .fetch();

        final List<StudyPreview> result = projectionStudyPreview(
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

        final Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyTypeEq(condition.type()),
                        studyCategoryIn(memberInterests),
                        studyIsNotTerminated()
                )
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                hasNext(pageable, result.size(), totalCount)
        );
    }

    private List<StudyPreview> projectionStudyPreview(
            final SortType sortType,
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
                .groupBy(study.id)
                .orderBy(study.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

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
        final List<Long> studyIds = query
                .select(study.id)
                .from(study)
                .leftJoin(favorite).on(favorite.studyId.eq(study.id))
                .where(whereConditions.toArray(Predicate[]::new))
                .groupBy(study.id)
                .orderBy(favorite.count().desc(), study.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (CollectionUtils.isEmpty(studyIds)) {
            return List.of();
        }

        final List<StudyPreview> preResult = query
                .select(studyPreviewProjection())
                .from(study)
                .where(study.id.in(studyIds))
                .fetch();

        final List<StudyPreview> result = new ArrayList<>();
        studyIds.forEach(
                studyId -> preResult.stream()
                        .filter(studyPreview -> studyPreview.getId().equals(studyId))
                        .forEach(result::add)
        );

        applyStudyHashtags(result, studyIds);
        applyLikeMarkingMembers(result, studyIds);
        return result;
    }

    private List<StudyPreview> projectionByReviewCount(
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final List<Long> studyIds = query
                .select(study.id)
                .from(study)
                .leftJoin(studyReview).on(studyReview.studyId.eq(study.id))
                .where(whereConditions.toArray(Predicate[]::new))
                .groupBy(study.id)
                .orderBy(studyReview.count().desc(), study.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (CollectionUtils.isEmpty(studyIds)) {
            return List.of();
        }

        final List<StudyPreview> preResult = query
                .select(studyPreviewProjection())
                .from(study)
                .where(study.id.in(studyIds))
                .fetch();

        final List<StudyPreview> result = new ArrayList<>();
        studyIds.forEach(
                studyId -> preResult.stream()
                        .filter(studyPreview -> studyPreview.getId().equals(studyId))
                        .forEach(result::add)
        );

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
                .select(
                        new QStudyPreview_HashtagSummary(
                                hashtag.study.id,
                                hashtag.name
                        )
                )
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
        final List<Favorite> favorites = query
                .select(favorite)
                .from(favorite)
                .where(favorite.studyId.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyLikeMarkingMembers(collectLikeMarkingMembers(study, favorites)));
    }

    private List<Long> collectLikeMarkingMembers(
            final StudyPreview study,
            final List<Favorite> favorites
    ) {
        return favorites
                .stream()
                .filter(favorite -> favorite.getStudyId().equals(study.getId()))
                .map(Favorite::getMemberId)
                .toList();
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

    private BooleanExpression studyCategoryEq(final Category category) {
        return study.category.eq(category);
    }

    private BooleanExpression studyCategoryIn(final List<Category> categories) {
        return study.category.in(categories);
    }

    private BooleanExpression studyIsNotTerminated() {
        return study.terminated.isFalse();
    }

    private BooleanExpression studyTypeEq(final String type) {
        return (type != null) ? study.type.eq(StudyType.from(type)) : null;
    }

    private BooleanExpression studyLocationProvinceEq(final String province) {
        return (province != null) ? study.location.province.eq(province) : null;
    }

    private BooleanExpression studyLocationCityEq(final String city) {
        return (city != null) ? study.location.city.eq(city) : null;
    }
}
