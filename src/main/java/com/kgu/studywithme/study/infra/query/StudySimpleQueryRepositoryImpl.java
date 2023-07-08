package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.member.domain.QMember;
import com.kgu.studywithme.study.domain.participant.ParticipantStatus;
import com.kgu.studywithme.study.domain.week.QWeek;
import com.kgu.studywithme.study.infra.query.dto.response.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.favorite.domain.QFavorite.favorite;
import static com.kgu.studywithme.study.domain.QStudy.study;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.study.domain.attendance.QAttendance.attendance;
import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;
import static com.kgu.studywithme.study.domain.participant.QParticipant.participant;
import static com.kgu.studywithme.study.domain.review.QReview.review;
import static com.kgu.studywithme.study.domain.week.attachment.QAttachment.attachment;
import static com.kgu.studywithme.study.domain.week.submit.QSubmit.submit;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudySimpleQueryRepositoryImpl implements StudySimpleQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember host = new QMember("host");

    @Override
    public List<SimpleStudy> findApplyStudyByMemberId(final Long memberId) {
        return query
                .selectDistinct(
                        new QSimpleStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail
                        )
                )
                .from(study)
                .innerJoin(participant).on(participant.study.id.eq(study.id))
                .where(
                        memberIdEq(memberId),
                        participateStatusEq(APPLY),
                        studyIsNotClosed()
                )
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleStudy> findParticipateStudyByMemberId(final Long memberId) {
        return query
                .selectDistinct(
                        new QSimpleStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail
                        )
                )
                .from(study)
                .innerJoin(study.participants.host, host)
                .leftJoin(participant).on(participant.study.id.eq(study.id))
                .where(
                        hostIdEq(memberId).or(participantIdEqAndApproveStatus(memberId)),
                        studyIsNotClosed()
                )
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleStudy> findFavoriteStudyByMemberId(final Long memberId) {
        return query
                .selectDistinct(
                        new QSimpleStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail
                        )
                )
                .from(study)
                .innerJoin(favorite).on(favorite.studyId.eq(study.id))
                .where(favorite.memberId.eq(memberId))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<SimpleGraduatedStudy> findGraduatedStudyByMemberId(final Long memberId) {
        return query
                .selectDistinct(
                        new QSimpleGraduatedStudy(
                                study.id,
                                study.name,
                                study.category,
                                study.thumbnail,
                                review.id,
                                review.content,
                                review.createdAt,
                                review.modifiedAt
                        )
                )
                .from(study)
                .innerJoin(participant).on(participant.study.id.eq(study.id))
                .leftJoin(review).on(review.study.id.eq(study.id))
                .where(memberIdEq(memberId), participateStatusEq(GRADUATED))
                .orderBy(study.id.desc())
                .fetch();
    }

    @Override
    public List<BasicWeekly> findAutoAttendanceAndPeriodEndWeek() {
        final LocalDateTime now = LocalDateTime.now();
        QWeek weekly = new QWeek("week");

        return query
                .select(
                        new QBasicWeekly(
                                weekly.study.id,
                                weekly.week
                        )
                )
                .from(weekly)
                .where(
                        weekly.autoAttendance.eq(true),
                        weekly.period.endDate.before(now)
                )
                .orderBy(weekly.study.id.asc(), weekly.week.asc())
                .fetch();
    }

    @Override
    public List<BasicAttendance> findNonAttendanceInformation() {
        return query
                .select(
                        new QBasicAttendance(
                                attendance.study.id,
                                attendance.week,
                                attendance.participant.id
                        )
                )
                .from(attendance)
                .where(attendance.status.eq(NON_ATTENDANCE))
                .orderBy(attendance.study.id.asc())
                .fetch();
    }

    @Override
    public boolean isStudyParticipant(
            final Long studyId,
            final Long memberId
    ) {
        List<Long> participantIds = query
                .select(participant.member.id)
                .from(participant)
                .where(
                        participant.study.id.eq(studyId),
                        participateStatusEq(APPROVE)
                )
                .fetch();

        return isStudyHost(studyId, memberId) || isMemberInParticipant(memberId, participantIds);
    }

    private boolean isStudyHost(
            final Long studyId,
            final Long memberId
    ) {
        return query
                .select(study.id)
                .from(study)
                .where(
                        study.id.eq(studyId),
                        hostIdEq(memberId)
                )
                .fetch()
                .size() == 1;
    }

    private boolean isMemberInParticipant(
            final Long memberId,
            final List<Long> participantIds
    ) {
        return participantIds.contains(memberId);
    }

    @Override
    public int getNextWeek(final Long studyId) {
        QWeek weekly = new QWeek("week");

        List<Integer> weeks = query
                .select(weekly.week)
                .from(weekly)
                .where(weekly.study.id.eq(studyId))
                .orderBy(weekly.week.desc())
                .fetch();

        return weeks.size() == 0 ? 1 : weeks.get(0) + 1;
    }

    @Override
    public boolean isLatestWeek(
            final Long studyId,
            final Integer week
    ) {
        QWeek weekly = new QWeek("week");

        List<Integer> weeks = query
                .select(weekly.week)
                .from(weekly)
                .where(weekly.study.id.eq(studyId))
                .orderBy(weekly.week.desc())
                .fetch();

        if (weeks.size() == 0) {
            return true;
        }

        return weeks.get(0).equals(week);
    }

    @Override
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    public void deleteSpecificWeek(
            final Long studyId,
            final Integer week
    ) {
        QWeek weekly = new QWeek("week");

        Long weekId = query
                .select(weekly.id)
                .from(weekly)
                .where(
                        weekly.study.id.eq(studyId),
                        weekly.week.eq(week)
                )
                .fetchOne();

        if (weekId != null) {
            query.delete(submit)
                    .where(submit.week.id.eq(weekId))
                    .execute();

            query.delete(attachment)
                    .where(attachment.week.id.eq(weekId))
                    .execute();

            query.delete(attendance)
                    .where(
                            attendance.study.id.eq(studyId),
                            attendance.week.eq(week)
                    )
                    .execute();

            query.delete(weekly)
                    .where(weekly.id.eq(weekId))
                    .execute();
        }
    }

    private BooleanExpression participateStatusEq(final ParticipantStatus status) {
        return (status != null) ? participant.status.eq(status) : null;
    }

    private BooleanExpression participantIdEqAndApproveStatus(final Long memberId) {
        return (memberId != null) ? participant.member.id.eq(memberId).and(participateStatusEq(APPROVE)) : null;
    }

    private BooleanExpression memberIdEq(final Long memberId) {
        return (memberId != null) ? participant.member.id.eq(memberId) : null;
    }

    private BooleanExpression hostIdEq(final Long memberId) {
        return (memberId != null) ? study.participants.host.id.eq(memberId) : null;
    }

    private BooleanExpression studyIsNotClosed() {
        return study.closed.eq(false);
    }
}
