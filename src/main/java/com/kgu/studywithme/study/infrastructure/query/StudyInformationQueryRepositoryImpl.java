package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.infrastructure.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.NoticeInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QAttendanceInformation_AttenadnceParticipant;
import com.kgu.studywithme.study.infrastructure.query.dto.QNoticeInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QNoticeInformation_CommentInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QReviewInformation_ReviewMetadata;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyApplicantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyBasicInformation_ParticipantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QStudyMember;
import com.kgu.studywithme.study.infrastructure.query.dto.QWeeklyInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.QWeeklyInformation_WeeklyAttachment;
import com.kgu.studywithme.study.infrastructure.query.dto.QWeeklyInformation_WeeklySubmit;
import com.kgu.studywithme.study.infrastructure.query.dto.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyMember;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyParticipantInformation;
import com.kgu.studywithme.study.infrastructure.query.dto.WeeklyInformation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.kgu.studywithme.member.domain.QMember.member;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.hashtag.QHashtag.hashtag;
import static com.kgu.studywithme.studyattendance.domain.QStudyAttendance.studyAttendance;
import static com.kgu.studywithme.studynotice.domain.QStudyNotice.studyNotice;
import static com.kgu.studywithme.studynotice.domain.comment.QStudyNoticeComment.studyNoticeComment;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.QStudyParticipant.studyParticipant;
import static com.kgu.studywithme.studyreview.domain.QStudyReview.studyReview;
import static com.kgu.studywithme.studyweekly.domain.QStudyWeekly.studyWeekly;
import static com.kgu.studywithme.studyweekly.domain.attachment.QStudyWeeklyAttachment.studyWeeklyAttachment;
import static com.kgu.studywithme.studyweekly.domain.submit.QStudyWeeklySubmit.studyWeeklySubmit;

@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyInformationQueryRepositoryImpl implements StudyInformationQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public StudyBasicInformation fetchBasicInformationById(final Long studyId) {
        // 스터디 기본 정보
        final StudyBasicInformation result = query
                .select(
                        new QStudyBasicInformation(
                                study.id,
                                study.name,
                                study.description,
                                study.category,
                                study.thumbnail,
                                study.type,
                                study.location,
                                study.recruitmentStatus,
                                study.capacity,
                                study.participantMembers,
                                study.graduationPolicy.minimumAttendance,
                                study.graduationPolicy.updateChance,
                                member.id,
                                member.nickname
                        )
                )
                .from(study)
                .innerJoin(member).on(member.id.eq(study.hostId))
                .where(study.id.eq(studyId))
                .fetchOne();

        if (result != null) {
            // 해시태그
            final List<String> hashtags = query
                    .select(hashtag.name)
                    .from(hashtag)
                    .where(hashtag.study.id.eq(studyId))
                    .fetch();
            result.applyHashtags(hashtags);

            // 참여자 정보
            final List<StudyBasicInformation.ParticipantInformation> participants = query
                    .select(
                            new QStudyBasicInformation_ParticipantInformation(
                                    member.id,
                                    member.nickname,
                                    member.gender,
                                    member.score,
                                    member.birth
                            )
                    )
                    .from(member)
                    .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                    .where(
                            studyParticipant.studyId.eq(studyId),
                            studyParticipant.status.eq(APPROVE)
                    )
                    .fetch();
            result.applyParticipants(participants);
        }

        return result;
    }

    @Override
    public ReviewInformation fetchReviewById(final Long studyId) {
        final List<ReviewInformation.ReviewMetadata> reviews = query
                .select(
                        new QReviewInformation_ReviewMetadata(
                                studyReview.id,
                                studyReview.content,
                                studyReview.lastModifiedAt,
                                member.id,
                                member.nickname
                        )
                )
                .from(studyReview)
                .innerJoin(member).on(member.id.eq(studyReview.writerId))
                .where(studyReview.studyId.eq(studyId))
                .orderBy(studyReview.id.desc())
                .fetch();

        final int graduateCount = query
                .select(studyParticipant.count())
                .from(studyParticipant)
                .where(
                        studyParticipant.studyId.eq(studyId),
                        studyParticipant.status.eq(GRADUATED)
                )
                .fetchOne()
                .intValue();

        return new ReviewInformation(reviews, graduateCount);
    }

    @Override
    public StudyParticipantInformation fetchParticipantById(final Long studyId) {
        final StudyMember host = query
                .select(
                        new QStudyMember(
                                member.id,
                                member.nickname
                        )
                )
                .from(member)
                .innerJoin(study).on(study.hostId.eq(member.id))
                .where(study.id.eq(studyId))
                .fetchOne();

        final List<StudyMember> participants = query
                .select(
                        new QStudyMember(
                                member.id,
                                member.nickname
                        )
                )
                .from(member)
                .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                .where(
                        studyParticipant.studyId.eq(studyId),
                        studyParticipant.memberId.ne(host.id()),
                        studyParticipant.status.eq(APPROVE)
                )
                .fetch();

        return new StudyParticipantInformation(host, participants);
    }

    @Override
    public List<StudyApplicantInformation> fetchApplicantById(final Long studyId) {
        return query
                .select(
                        new QStudyApplicantInformation(
                                member.id,
                                member.nickname,
                                member.score,
                                studyParticipant.createdAt
                        )
                )
                .from(member)
                .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                .where(
                        studyParticipant.studyId.eq(studyId),
                        studyParticipant.status.eq(APPLY)
                )
                .orderBy(studyParticipant.id.desc())
                .fetch();
    }

    @Override
    public List<NoticeInformation> fetchNoticeById(final Long studyId) {
        final List<NoticeInformation> notices = query
                .select(
                        new QNoticeInformation(
                                studyNotice.id,
                                studyNotice.title,
                                studyNotice.content,
                                studyNotice.createdAt,
                                studyNotice.lastModifiedAt,
                                member.id,
                                member.nickname
                        )
                )
                .from(studyNotice)
                .innerJoin(member).on(member.id.eq(studyNotice.writerId))
                .where(studyNotice.studyId.eq(studyId))
                .orderBy(studyNotice.id.desc())
                .fetch();

        final List<Long> noticeIds = notices.stream()
                .map(NoticeInformation::getId)
                .toList();
        final List<NoticeInformation.CommentInformation> comments = query
                .select(
                        new QNoticeInformation_CommentInformation(
                                studyNoticeComment.id,
                                studyNoticeComment.notice.id,
                                studyNoticeComment.content,
                                studyNoticeComment.lastModifiedAt,
                                member.id,
                                member.nickname
                        )
                )
                .from(studyNoticeComment)
                .innerJoin(member).on(member.id.eq(studyNoticeComment.writerId))
                .where(studyNoticeComment.notice.id.in(noticeIds))
                .orderBy(studyNoticeComment.id.desc())
                .fetch();

        notices.forEach(
                notice -> notice.applyComments(
                        comments.stream()
                                .filter(comment -> comment.noticeId().equals(notice.getId()))
                                .toList()
                )
        );
        return notices;
    }

    @Override
    public List<AttendanceInformation> fetchAttendanceById(final Long studyId) {
        final List<AttendanceInformation.AttenadnceParticipant> result = query
                .select(
                        new QAttendanceInformation_AttenadnceParticipant(
                                member.id,
                                member.nickname,
                                studyAttendance.week,
                                studyAttendance.status
                        )
                )
                .from(studyAttendance)
                .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(studyAttendance.participantId))
                .innerJoin(member).on(member.id.eq(studyParticipant.memberId))
                .where(
                        studyAttendance.studyId.eq(studyId),
                        studyParticipant.status.eq(APPROVE)
                )
                .orderBy(studyParticipant.lastModifiedAt.asc(), studyAttendance.week.asc())
                .fetch();

        return result.stream()
                .collect(
                        Collectors.groupingBy(
                                AttendanceInformation.AttenadnceParticipant::participant,
                                LinkedHashMap::new,
                                Collectors.mapping(
                                        value -> new AttendanceInformation.AttendanceSummary(
                                                value.week(),
                                                value.attendanceStatus()
                                        ),
                                        Collectors.toList()
                                )
                        )
                )
                .entrySet().stream()
                .map(entry -> new AttendanceInformation(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<WeeklyInformation> fetchWeeklyById(final Long studyId) {
        final List<WeeklyInformation> weeklyInformations = query
                .select(
                        new QWeeklyInformation(
                                studyWeekly.id,
                                studyWeekly.title,
                                studyWeekly.content,
                                studyWeekly.week,
                                studyWeekly.period,
                                studyWeekly.assignmentExists,
                                studyWeekly.autoAttendance,
                                member.id,
                                member.nickname
                        )
                )
                .from(studyWeekly)
                .innerJoin(member).on(member.id.eq(studyWeekly.creatorId))
                .where(studyWeekly.studyId.eq(studyId))
                .orderBy(studyWeekly.id.desc())
                .fetch();

        if (!weeklyInformations.isEmpty()) {
            final List<Long> weeklyIds = weeklyInformations.stream()
                    .map(WeeklyInformation::getId)
                    .toList();

            final List<WeeklyInformation.WeeklyAttachment> attachments = query
                    .select(
                            new QWeeklyInformation_WeeklyAttachment(
                                    studyWeeklyAttachment.studyWeekly.id,
                                    studyWeeklyAttachment.uploadAttachment.uploadFileName,
                                    studyWeeklyAttachment.uploadAttachment.link
                            )
                    )
                    .from(studyWeeklyAttachment)
                    .where(studyWeeklyAttachment.studyWeekly.id.in(weeklyIds))
                    .fetch();
            weeklyInformations.forEach(
                    weekly -> weekly.applyAttachments(
                            attachments.stream()
                                    .filter(attachment -> attachment.weeklyId().equals(weekly.getId()))
                                    .toList()
                    )
            );

            final List<WeeklyInformation.WeeklySubmit> submits = query
                    .select(
                            new QWeeklyInformation_WeeklySubmit(
                                    member.id,
                                    member.nickname,
                                    studyWeeklySubmit.studyWeekly.id,
                                    studyWeeklySubmit.uploadAssignment
                            )
                    )
                    .from(studyWeeklySubmit)
                    .innerJoin(member).on(member.id.eq(studyWeeklySubmit.participantId))
                    .where(studyWeeklySubmit.studyWeekly.id.in(weeklyIds))
                    .fetch();
            weeklyInformations.forEach(
                    weekly -> weekly.applySubmits(
                            submits.stream()
                                    .filter(submit -> submit.weeklyId().equals(weekly.getId()))
                                    .toList()
                    )
            );
        }

        return weeklyInformations;
    }
}
