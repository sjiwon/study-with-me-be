package com.kgu.studywithme.member.domain.repository.query;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.member.domain.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.domain.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPrivateInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.QAppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.QAttendanceRatio;
import com.kgu.studywithme.member.domain.repository.query.dto.QGraduatedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.QLikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.QMemberPrivateInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.QMemberPublicInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.QParticipateStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.QReceivedReview;
import com.kgu.studywithme.member.domain.repository.query.dto.ReceivedReview;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kgu.studywithme.favorite.domain.model.QFavorite.favorite;
import static com.kgu.studywithme.member.domain.model.QInterest.interest;
import static com.kgu.studywithme.member.domain.model.QMember.member;
import static com.kgu.studywithme.memberreview.domain.model.QMemberReview.memberReview;
import static com.kgu.studywithme.study.domain.model.QStudy.study;
import static com.kgu.studywithme.studyattendance.domain.model.QStudyAttendance.studyAttendance;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.QStudyParticipant.studyParticipant;
import static com.kgu.studywithme.studyreview.domain.model.QStudyReview.studyReview;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberInformationRepositoryImpl implements MemberInformationRepository {
    private final JPAQueryFactory query;

    @Override
    public MemberPublicInformation fetchPublicInformationById(final Long memberId) {
        final MemberPublicInformation result = query
                .select(new QMemberPublicInformation(
                        member.id,
                        member.name,
                        member.nickname,
                        member.email,
                        member.birth,
                        member.gender,
                        member.address,
                        member.score
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        if (result != null) {
            final List<String> interests = query
                    .select(interest.category)
                    .from(interest)
                    .innerJoin(interest.member, member)
                    .where(member.id.eq(memberId))
                    .fetch()
                    .stream()
                    .map(Category::getName)
                    .toList();

            result.applyInterests(interests);
        }

        return result;
    }

    @Override
    public List<ParticipateStudy> fetchParticipateStudyById(final Long memberId) {
        return query
                .select(new QParticipateStudy(
                        study.id,
                        study.name,
                        study.category,
                        study.thumbnail
                ))
                .from(study)
                .innerJoin(studyParticipant).on(studyParticipant.study.id.eq(study.id))
                .where(
                        studyParticipant.member.id.eq(memberId),
                        studyParticipant.status.eq(APPROVE)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();
    }

    @Override
    public List<GraduatedStudy> fetchGraduatedStudyById(final Long memberId) {
        final List<GraduatedStudy> result = query
                .select(new QGraduatedStudy(
                        study.id,
                        study.name,
                        study.category,
                        study.thumbnail
                ))
                .from(study)
                .innerJoin(studyParticipant).on(studyParticipant.study.id.eq(study.id))
                .where(
                        studyParticipant.member.id.eq(memberId),
                        studyParticipant.status.eq(GRADUATED)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();

        if (!result.isEmpty()) {
            final List<StudyReview> writtenReviews = query
                    .selectFrom(studyReview)
                    .innerJoin(studyReview.study).fetchJoin()
                    .where(studyReview.writer.id.eq(memberId))
                    .fetch();

            result.forEach(
                    graduatedStudy -> graduatedStudy.applyWrittenReview(
                            writtenReviews.stream()
                                    .filter(writtenReview -> writtenReview.getStudy().getId().equals(graduatedStudy.getId()))
                                    .map(writtenReview -> new GraduatedStudy.WrittenReview(
                                            writtenReview.getId(),
                                            writtenReview.getContent(),
                                            writtenReview.getCreatedAt(),
                                            writtenReview.getLastModifiedAt()
                                    ))
                                    .findFirst()
                                    .orElse(null)
                    )
            );
        }

        return result;
    }

    @Override
    public List<ReceivedReview> fetchReceivedReviewById(final Long memberId) {
        return query
                .select(new QReceivedReview(
                        memberReview.content,
                        memberReview.lastModifiedAt
                ))
                .from(memberReview)
                .where(memberReview.reviewee.id.eq(memberId))
                .orderBy(memberReview.id.desc())
                .fetch();
    }

    @Override
    public List<AttendanceRatio> fetchAttendanceRatioById(final Long memberId) {
        final List<AttendanceRatio> fetchResult = query
                .select(new QAttendanceRatio(
                        studyAttendance.status,
                        studyAttendance.count()
                ))
                .from(studyAttendance)
                .where(studyAttendance.participant.id.eq(memberId))
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
                        .orElse(new AttendanceRatio(status, 0)))
                .toList();
    }

    @Override
    public MemberPrivateInformation fetchPrivateInformationById(final Long memberId) {
        final MemberPrivateInformation result = query
                .select(new QMemberPrivateInformation(
                        member.id,
                        member.name,
                        member.nickname,
                        member.email,
                        member.birth,
                        member.phone,
                        member.gender,
                        member.address,
                        member.score
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        if (result != null) {
            final List<String> interests = query
                    .select(interest.category)
                    .from(interest)
                    .innerJoin(interest.member, member)
                    .where(member.id.eq(memberId))
                    .fetch()
                    .stream()
                    .map(Category::getName)
                    .toList();

            result.applyInterests(interests);
        }

        return result;
    }

    @Override
    public List<AppliedStudy> fetchAppliedStudyById(final Long memberId) {
        return query
                .select(new QAppliedStudy(
                        study.id,
                        study.name,
                        study.category,
                        study.thumbnail
                ))
                .from(study)
                .innerJoin(studyParticipant).on(studyParticipant.study.id.eq(study.id))
                .where(
                        studyParticipant.member.id.eq(memberId),
                        studyParticipant.status.eq(APPLY)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();
    }

    @Override
    public List<LikeMarkedStudy> fetchLikeMarkedStudyById(final Long memberId) {
        return query
                .select(new QLikeMarkedStudy(
                        study.id,
                        study.name,
                        study.category,
                        study.thumbnail
                ))
                .from(study)
                .innerJoin(favorite).on(favorite.study.id.eq(study.id))
                .where(favorite.member.id.eq(memberId))
                .orderBy(favorite.id.desc())
                .fetch();
    }
}
