package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.BasicStudy;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.QBasicHashtag;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.response.QBasicStudy;
import com.kgu.studywithme.study.utils.StudyCategoryCondition;
import com.kgu.studywithme.study.utils.StudyRecommendCondition;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.interest.QInterest.interest;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.study.domain.review.QReview.review;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_FAVORITE;
import static com.kgu.studywithme.study.utils.PagingConstants.SORT_REVIEW;
import static com.querydsl.jpa.JPAExpressions.select;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyCategoryQueryRepositoryImpl implements StudyCategoryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<BasicStudy> findStudyByCategory(
            final StudyCategoryCondition condition,
            final Pageable pageable
    ) {
        JPAQuery<BasicStudy> fetchQuery = query
                .select(assembleStudyProjections())
                .from(study)
                .where(
                        categoryEq(condition.category()),
                        studyType(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotClosed()
                )
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<BasicStudy> result = makeFetchQueryResult(fetchQuery, condition.sort());
        Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        categoryEq(condition.category()),
                        studyType(condition.type()),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotClosed()
                )
                .fetchOne();

        return new SliceImpl<>(result, pageable, validateHasNext(pageable, result.size(), totalCount));
    }

    @Override
    public Slice<BasicStudy> findStudyByRecommend(
            final StudyRecommendCondition condition,
            final Pageable pageable
    ) {
        List<Category> memberInterests = query
                .select(interest.category)
                .from(interest)
                .where(interest.member.id.eq(condition.memberId()))
                .fetch();

        JPAQuery<BasicStudy> fetchQuery = query
                .select(assembleStudyProjections())
                .from(study)
                .leftJoin(participant).on(
                        participant.study.id.eq(study.id),
                        participant.status.eq(APPROVE)
                )
                .where(
                        studyType(condition.type()),
                        studyCategoryIn(memberInterests),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotClosed()
                )
                .groupBy(study.id)
                .orderBy(orderBySortType(condition.sort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<BasicStudy> result = makeFetchQueryResult(fetchQuery, condition.sort());
        Long totalCount = query
                .select(study.id.count())
                .from(study)
                .where(
                        studyType(condition.type()),
                        studyCategoryIn(memberInterests),
                        studyLocationProvinceEq(condition.province()),
                        studyLocationCityEq(condition.city()),
                        studyIsNotClosed()
                )
                .fetchOne();

        return new SliceImpl<>(result, pageable, validateHasNext(pageable, result.size(), totalCount));
    }

    public static ConstructorExpression<BasicStudy> assembleStudyProjections() {
        return new QBasicStudy(
                study.id,
                study.name,
                study.description,
                study.category,
                study.thumbnail,
                study.type,
                study.recruitmentStatus,
                select(participant.count().intValue())
                        .from(participant)
                        .where(
                                participant.study.id.eq(study.id),
                                participant.status.eq(APPROVE)
                        ),
                study.participants.capacity,
                study.createdAt
        );
    }

    private List<OrderSpecifier<?>> orderBySortType(final String sort) {
        List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sort) {
            case SORT_FAVORITE -> orderBy.addAll(List.of(favorite.count().desc(), study.id.desc()));
            case SORT_REVIEW -> orderBy.addAll(List.of(review.count().desc(), study.id.desc()));
            default -> orderBy.add(study.id.desc());
        }

        return orderBy;
    }

    private List<BasicStudy> makeFetchQueryResult(
            final JPAQuery<BasicStudy> fetchQuery,
            final String sort
    ) {
        List<BasicStudy> result = addJoinBySortOption(fetchQuery, sort);
        List<Long> studyIds = result.stream()
                .map(BasicStudy::getId)
                .toList();

        applyStudyHashtags(studyIds, result);
        applyStudyFavoriteMarkingMembers(studyIds, result);
        return result;
    }

    private List<BasicStudy> addJoinBySortOption(
            final JPAQuery<BasicStudy> fetchQuery,
            final String sort
    ) {
        return switch (sort) {
            case SORT_FAVORITE -> fetchQuery
                    .leftJoin(favorite).on(favorite.studyId.eq(study.id))
                    .fetch();
            case SORT_REVIEW -> fetchQuery
                    .leftJoin(review).on(review.study.id.eq(study.id))
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private void applyStudyHashtags(
            final List<Long> studyIds,
            final List<BasicStudy> result
    ) {
        List<BasicHashtag> hashtags = query
                .select(new QBasicHashtag(study.id, hashtag.name))
                .from(hashtag)
                .innerJoin(study).on(study.id.eq(hashtag.study.id))
                .where(study.id.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyHashtags(collectHashtags(hashtags, study)));
    }

    private List<String> collectHashtags(
            final List<BasicHashtag> hashtags,
            final BasicStudy study
    ) {
        return hashtags
                .stream()
                .filter(hashtag -> hashtag.studyId().equals(study.getId()))
                .map(BasicHashtag::name)
                .toList();
    }

    private void applyStudyFavoriteMarkingMembers(
            final List<Long> studyIds,
            final List<BasicStudy> result
    ) {
        List<Favorite> favorites = query
                .select(favorite)
                .from(favorite)
                .where(favorite.studyId.in(studyIds))
                .fetch();

        result.forEach(study -> study.applyFavoriteMarkingMembers(collectFavoriteMarkingMembers(favorites, study)));
    }

    private List<Long> collectFavoriteMarkingMembers(
            final List<Favorite> favorites,
            final BasicStudy study
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

    private BooleanExpression categoryEq(final Category category) {
        return (category != null) ? study.category.eq(category) : null;
    }

    private BooleanExpression studyType(final String type) {
        if (!hasText(type)) {
            return null;
        }

        return "online".equals(type) ? study.type.eq(ONLINE) : study.type.eq(OFFLINE);
    }

    private BooleanExpression studyCategoryIn(final List<Category> memberInterests) {
        return (memberInterests != null) ? study.category.in(memberInterests) : null;
    }

    private BooleanExpression studyLocationProvinceEq(final String province) {
        return hasText(province) ? study.location.province.eq(province) : null;
    }

    private BooleanExpression studyLocationCityEq(final String city) {
        return hasText(city) ? study.location.city.eq(city) : null;
    }

    private BooleanExpression studyIsNotClosed() {
        return study.closed.eq(false);
    }

    private boolean hasText(final String str) {
        return StringUtils.hasText(str);
    }
}