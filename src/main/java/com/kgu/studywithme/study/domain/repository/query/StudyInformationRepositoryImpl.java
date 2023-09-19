package com.kgu.studywithme.study.domain.repository.query;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.study.domain.repository.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.NoticeInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QAttendanceInformation_AttendanceParticipant;
import com.kgu.studywithme.study.domain.repository.query.dto.QNoticeInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QNoticeInformation_CommentInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QReviewInformation_ReviewMetadata;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyApplicantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyBasicInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyBasicInformation_ParticipantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QStudyMember;
import com.kgu.studywithme.study.domain.repository.query.dto.QWeeklyInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.QWeeklyInformation_WeeklyAttachment;
import com.kgu.studywithme.study.domain.repository.query.dto.QWeeklyInformation_WeeklySubmit;
import com.kgu.studywithme.study.domain.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyMember;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyParticipantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.WeeklyInformation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.kgu.studywithme.member.domain.model.QMember.member;
import static com.kgu.studywithme.study.domain.model.QHashtag.hashtag;
import static com.kgu.studywithme.study.domain.model.QStudy.study;
import static com.kgu.studywithme.studyattendance.domain.model.QStudyAttendance.studyAttendance;
import static com.kgu.studywithme.studynotice.domain.model.QStudyNotice.studyNotice;
import static com.kgu.studywithme.studynotice.domain.model.QStudyNoticeComment.studyNoticeComment;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.QStudyParticipant.studyParticipant;
import static com.kgu.studywithme.studyreview.domain.model.QStudyReview.studyReview;
import static com.kgu.studywithme.studyweekly.domain.model.QStudyWeekly.studyWeekly;
import static com.kgu.studywithme.studyweekly.domain.model.QStudyWeeklyAttachment.studyWeeklyAttachment;
import static com.kgu.studywithme.studyweekly.domain.model.QStudyWeeklySubmit.studyWeeklySubmit;

@Repository
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyInformationRepositoryImpl implements StudyInformationRepository {
    private final JPAQueryFactory query;

    @Override
    public StudyBasicInformation fetchBasicInformationById(final Long studyId) {
        // 스터디 기본 정보
        final StudyBasicInformation result = query
                .select(new QStudyBasicInformation(
                        study.id,
                        study.name,
                        study.description,
                        study.category,
                        study.thumbnail,
                        study.type,
                        study.location,
                        study.recruitmentStatus,
                        study.capacity,
                        study.participants,
                        study.graduationPolicy.minimumAttendance,
                        study.graduationPolicy.updateChance,
                        member.id,
                        member.nickname
                ))
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
                    .select(new QStudyBasicInformation_ParticipantInformation(
                            member.id,
                            member.nickname,
                            member.gender,
                            member.score,
                            member.birth
                    ))
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
                .select(new QReviewInformation_ReviewMetadata(
                        studyReview.id,
                        studyReview.content,
                        studyReview.lastModifiedAt,
                        member.id,
                        member.nickname
                ))
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
                .select(new QStudyMember(
                        member.id,
                        member.nickname
                ))
                .from(member)
                .innerJoin(study).on(study.hostId.eq(member.id))
                .where(study.id.eq(studyId))
                .fetchOne();

        final List<StudyMember> participants = query
                .select(new QStudyMember(
                        member.id,
                        member.nickname
                ))
                .from(member)
                .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(member.id))
                .where(
                        studyParticipant.studyId.eq(studyId),
                        studyParticipant.status.eq(APPROVE)
                )
                .fetch()
                .stream()
                .filter(studyMember -> !studyMember.id().equals(host.id()))
                .toList();

        return new StudyParticipantInformation(host, participants);
    }

    @Override
    public List<StudyApplicantInformation> fetchApplicantById(final Long studyId) {
        return query
                .select(new QStudyApplicantInformation(
                        member.id,
                        member.nickname,
                        member.score,
                        studyParticipant.createdAt
                ))
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
                .select(new QNoticeInformation(
                        studyNotice.id,
                        studyNotice.title,
                        studyNotice.content,
                        studyNotice.createdAt,
                        studyNotice.lastModifiedAt,
                        member.id,
                        member.nickname
                ))
                .from(studyNotice)
                .innerJoin(member).on(member.id.eq(studyNotice.writerId))
                .where(studyNotice.studyId.eq(studyId))
                .orderBy(studyNotice.id.desc())
                .fetch();

        if (!notices.isEmpty()) {
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
                    .fetch();

            notices.forEach(notice -> notice.applyComments(
                    comments.stream()
                            .filter(comment -> comment.noticeId().equals(notice.getId()))
                            .toList()
            ));
        }

        return notices;
    }

    @Override
    public List<AttendanceInformation> fetchAttendanceById(final Long studyId) {
        final List<AttendanceInformation.AttendanceParticipant> result = query
                .select(new QAttendanceInformation_AttendanceParticipant(
                        member.id,
                        member.nickname,
                        studyAttendance.week,
                        studyAttendance.status
                ))
                .from(studyAttendance)
                .innerJoin(studyParticipant).on(studyParticipant.memberId.eq(studyAttendance.participantId))
                .innerJoin(member).on(member.id.eq(studyParticipant.memberId))
                .where(
                        studyAttendance.studyId.eq(studyId),
                        studyParticipant.status.eq(APPROVE)
                )
                .orderBy(studyAttendance.week.asc())
                .fetch();

        if (result.isEmpty()) {
            return List.of();
        }

        return result.stream()
                .collect(Collectors.groupingBy(
                        AttendanceInformation.AttendanceParticipant::participant,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                value -> new AttendanceInformation.AttendanceSummary(
                                        value.week(),
                                        value.attendanceStatus()
                                ),
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .map(entry -> new AttendanceInformation(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<WeeklyInformation> fetchWeeklyById(final Long studyId) {
        final List<WeeklyInformation> weeklyInformations = query
                .select(new QWeeklyInformation(
                        studyWeekly.id,
                        studyWeekly.title,
                        studyWeekly.content,
                        studyWeekly.week,
                        studyWeekly.period,
                        studyWeekly.assignmentExists,
                        studyWeekly.autoAttendance,
                        member.id,
                        member.nickname
                ))
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
                    .select(new QWeeklyInformation_WeeklyAttachment(
                            studyWeeklyAttachment.weekly.id,
                            studyWeeklyAttachment.uploadAttachment.uploadFileName,
                            studyWeeklyAttachment.uploadAttachment.link
                    ))
                    .from(studyWeeklyAttachment)
                    .where(studyWeeklyAttachment.weekly.id.in(weeklyIds))
                    .fetch();
            weeklyInformations.forEach(weekly -> weekly.applyAttachments(
                    attachments.stream()
                            .filter(attachment -> attachment.weeklyId().equals(weekly.getId()))
                            .toList()
            ));

            final List<WeeklyInformation.WeeklySubmit> submits = query
                    .select(new QWeeklyInformation_WeeklySubmit(
                            member.id,
                            member.nickname,
                            studyWeeklySubmit.weekly.id,
                            studyWeeklySubmit.uploadAssignment
                    ))
                    .from(studyWeeklySubmit)
                    .innerJoin(member).on(member.id.eq(studyWeeklySubmit.participantId))
                    .where(studyWeeklySubmit.weekly.id.in(weeklyIds))
                    .fetch();
            weeklyInformations.forEach(weekly -> weekly.applySubmits(
                    submits.stream()
                            .filter(submit -> submit.weeklyId().equals(weekly.getId()))
                            .toList()
            ));
        }

        return weeklyInformations;
    }
}
