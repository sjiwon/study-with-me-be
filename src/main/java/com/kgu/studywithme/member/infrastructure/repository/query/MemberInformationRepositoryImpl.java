package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.*;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.QMember.member;
import static com.kgu.studywithme.member.domain.interest.QInterest.interest;
import static com.kgu.studywithme.memberreview.domain.QMemberReview.memberReview;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.*;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;
import static com.kgu.studywithme.studyreview.domain.QStudyReview.studyReview;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberInformationRepositoryImpl implements MemberInformationRepository {
    private final JPAQueryFactory query;

    @Override
    public MemberPublicInformation fetchPublicInformationById(final Long memberId) {
        final MemberPublicInformation result = query
                .select(
                        new QMemberPublicInformation(
                                member.id,
                                member.name,
                                member.nickname,
                                member.email,
                                member.birth,
                                member.gender,
                                member.region,
                                member.score
                        )
                )
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        if (result != null) {
            final List<Category> interests = query
                    .select(interest.category)
                    .from(interest)
                    .innerJoin(interest.member, member)
                    .where(member.id.eq(memberId))
                    .fetch();

            result.applyInterests(
                    interests.stream()
                            .map(Category::getName)
                            .toList()
            );
        }

        return result;
    }

    @Override
    public List<ParticipateStudy> fetchParticipateStudyById(final Long memberId) {
        return query
                .select(
                        new QParticipateStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail
                        )
                )
                .from(study)
                .innerJoin(studyParticipant).on(studyParticipant.studyId.eq(study.id))
                .where(
                        studyParticipant.memberId.eq(memberId),
                        studyParticipant.status.eq(APPROVE)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();
    }

    @Override
    public List<GraduatedStudy> fetchGraduatedStudyById(final Long memberId) {
        return query
                .select(
                        new QGraduatedStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail,
                                studyReview.id,
                                studyReview.content,
                                studyReview.createdAt,
                                studyReview.lastModifiedAt
                        )
                )
                .from(study)
                .innerJoin(studyParticipant).on(studyParticipant.studyId.eq(study.id))
                .leftJoin(studyReview).on(studyReview.studyId.eq(study.id))
                .where(
                        studyParticipant.memberId.eq(memberId),
                        studyParticipant.status.eq(GRADUATED)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();
    }

    @Override
    public List<ReceivedReview> fetchReceivedReviewById(final Long memberId) {
        return query
                .select(
                        new QReceivedReview(
                                memberReview.content,
                                memberReview.lastModifiedAt
                        )
                )
                .from(memberReview)
                .where(memberReview.revieweeId.eq(memberId))
                .orderBy(memberReview.id.desc())
                .fetch();
    }

    @Override
    public List<AttendanceRatio> fetchAttendanceRatioById(final Long memberId) {
        final List<AttendanceRatio> fetchResult = query
                .select(
                        new QAttendanceRatio(
                                studyAttendance.status,
                                studyAttendance.count().intValue()
                        )
                )
                .from(studyAttendance)
                .where(studyAttendance.participantId.eq(memberId))
                .groupBy(studyAttendance.status)
                .fetch();

        return includeMissingAttendanceStatus(fetchResult);
    }

    private List<AttendanceRatio> includeMissingAttendanceStatus(final List<AttendanceRatio> fetchResult) {
        return AttendanceStatus.getAttendanceStatuses()
                .stream()
                .map(status -> fetchResult.stream()
                        .filter(ratio -> ratio.status() == status)
                        .findFirst()
                        .orElse(new AttendanceRatio(status, 0))
                )
                .toList();
    }

    @Override
    public MemberPrivateInformation fetchPrivateInformationById(final Long memberId) {
        final MemberPrivateInformation result = query
                .select(
                        new QMemberPrivateInformation(
                                member.id,
                                member.name,
                                member.nickname,
                                member.email,
                                member.birth,
                                member.phone,
                                member.gender,
                                member.region,
                                member.score,
                                member.emailOptIn
                        )
                )
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        if (result != null) {
            final List<Category> interests = query
                    .select(interest.category)
                    .from(interest)
                    .innerJoin(interest.member, member)
                    .where(member.id.eq(memberId))
                    .fetch();

            result.applyInterests(
                    interests.stream()
                            .map(Category::getName)
                            .toList()
            );
        }

        return result;
    }

    @Override
    public List<AppliedStudy> fetchAppliedStudyById(final Long memberId) {
        return query
                .select(
                        new QAppliedStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail
                        )
                )
                .from(study)
                .innerJoin(studyParticipant).on(studyParticipant.studyId.eq(study.id))
                .where(
                        studyParticipant.memberId.eq(memberId),
                        studyParticipant.status.eq(APPLY)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();
    }

    @Override
    public List<LikeMarkedStudy> fetchLikeMarkedStudyById(final Long memberId) {
        return query
                .select(
                        new QLikeMarkedStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail
                        )
                )
                .from(study)
                .innerJoin(favorite).on(favorite.studyId.eq(study.id))
                .where(favorite.memberId.eq(memberId))
                .orderBy(favorite.id.desc())
                .fetch();
    }
}
