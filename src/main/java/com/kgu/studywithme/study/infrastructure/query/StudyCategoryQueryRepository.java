package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.application.adapter.StudyCategoryQueryRepositoryAdapter;
import com.kgu.studywithme.study.domain.StudyType;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyPreview;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyPreview_HashtagSummary;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.PagingConstants.SortType;
import com.kgu.studywithme.study.utils.QueryStudyByCategoryCondition;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.interest.QInterest.interest;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;
import static com.kgu.studywithme.studyreview.domain.QStudyReview.studyReview;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyCategoryQueryRepository implements StudyCategoryQueryRepositoryAdapter {
    private final JPAQueryFactory query;

    @Override
    public Slice<StudyPreview> fetchStudyByCategory(
            final QueryStudyByCategoryCondition condition,
            final Pageable pageable
    ) {
        final JPAQuery<StudyPreview> fetchQuery = query
                .select(
                        new QStudyPreview(
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
                        )
                )
                .from(study)
                .where(
                        studyCategoryEq(condition.category()),
                        studyTypeEq(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotTerminated()
                )
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        final List<StudyPreview> result = makeFetchQueryResult(fetchQuery, condition.sort());
        final Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        studyCategoryEq(condition.category()),
                        studyTypeEq(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotTerminated()
                )
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                validateHasNext(pageable, result.size(), totalCount)
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

        final JPAQuery<StudyPreview> fetchQuery = query
                .select(
                        new QStudyPreview(
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
                        )
                )
                .from(study)
                .where(
                        studyCategoryIn(memberInterests),
                        studyTypeEq(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotTerminated()
                )
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        final List<StudyPreview> result = makeFetchQueryResult(fetchQuery, condition.sort());
        final Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        studyCategoryIn(memberInterests),
                        studyTypeEq(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotTerminated()
                )
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                validateHasNext(pageable, result.size(), totalCount)
        );
    }

    private List<OrderSpecifier<?>> orderBySortType(final SortType sort) {
        final List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sort) {
            case FAVORITE -> orderBy.addAll(List.of(favorite.count().desc(), study.id.desc()));
            case REVIEW -> orderBy.addAll(List.of(studyReview.count().desc(), study.id.desc()));
            default -> orderBy.add(study.id.desc());
        }

        return orderBy;
    }

    private List<StudyPreview> makeFetchQueryResult(
            final JPAQuery<StudyPreview> fetchQuery,
            final SortType sort
    ) {
        final List<StudyPreview> result = addJoinBySortType(fetchQuery, sort);
        final List<Long> studyIds = result.stream()
                .map(StudyPreview::getId)
                .toList();

        applyStudyHashtags(result, studyIds);
        applyLikeMarkingMembers(result, studyIds);
        return result;
    }

    private List<StudyPreview> addJoinBySortType(
            final JPAQuery<StudyPreview> fetchQuery,
            final SortType sort
    ) {
        return switch (sort) {
            case FAVORITE -> fetchQuery
                    .leftJoin(favorite).on(favorite.studyId.eq(study.id))
                    .fetch();
            case REVIEW -> fetchQuery
                    .leftJoin(studyReview).on(studyReview.studyId.eq(study.id))
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private void applyStudyHashtags(
            final List<StudyPreview> result,
            final List<Long> studyIds
    ) {
        final List<StudyPreview.HashtagSummary> hashtags = query
                .select(
                        new QStudyPreview_HashtagSummary(
                                study.id,
                                hashtag.name
                        )
                )
                .from(hashtag)
                .innerJoin(study).on(study.id.eq(hashtag.study.id))
                .where(study.id.in(studyIds))
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

    private boolean validateHasNext(
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

    private BooleanExpression studyTypeEq(final String type) {
        if (!StringUtils.hasText(type)) {
            return null;
        }

        return study.type.eq(StudyType.from(type));
    }

    private BooleanExpression studyLocationProvinceEq(final String province) {
        return (province != null) ? study.location.province.eq(province) : null;
    }

    private BooleanExpression studyLocationCityEq(final String city) {
        return (city != null) ? study.location.city.eq(city) : null;
    }

    private BooleanExpression studyIsNotTerminated() {
        return study.terminated.isFalse();
    }
}
