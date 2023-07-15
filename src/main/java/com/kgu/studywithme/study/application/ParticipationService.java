package com.kgu.studywithme.study.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.service.QueryMemberByIdService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import com.kgu.studywithme.study.event.StudyApprovedEvent;
import com.kgu.studywithme.study.event.StudyGraduatedEvent;
import com.kgu.studywithme.study.event.StudyRejectedEvent;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class ParticipationService {
    private final StudyFindService studyFindService;
    private final QueryMemberByIdService queryMemberByIdService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @StudyWithMeWritableTransactional
    public void apply(
            final Long studyId,
            final Long memberId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member member = queryMemberByIdService.findById(memberId);

        study.applyParticipation(member);
    }

    @StudyWithMeWritableTransactional
    public void applyCancel(
            final Long studyId,
            final Long applierId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member applier = queryMemberByIdService.findById(applierId);
        study.validateMemberIsApplier(applier);

        participantRepository.deleteParticipantAssociatedMember(study, applier);
    }

    @StudyWithMeWritableTransactional
    public void approve(
            final Long studyId,
            final Long applierId,
            final Long hostId
    ) {
        final Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        final Member applier = queryMemberByIdService.findById(applierId);

        study.approveParticipation(applier);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyApprovedEvent(
                            applier.getEmailValue(),
                            applier.getNicknameValue(),
                            study.getNameValue()
                    )
            );
        }
    }

    @StudyWithMeWritableTransactional
    public void reject(
            final Long studyId,
            final Long applierId,
            final Long hostId,
            final String reason
    ) {
        final Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        final Member applier = queryMemberByIdService.findById(applierId);

        study.rejectParticipation(applier);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyRejectedEvent(
                            applier.getEmailValue(),
                            applier.getNicknameValue(),
                            study.getNameValue(),
                            reason
                    )
            );
        }
    }

    @StudyWithMeWritableTransactional
    public void cancel(
            final Long studyId,
            final Long participantId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member participant = queryMemberByIdService.findById(participantId);

        study.cancelParticipation(participant);
    }

    @StudyWithMeWritableTransactional
    public void delegateAuthority(
            final Long studyId,
            final Long participantId,
            final Long hostId
    ) {
        final Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        final Member newHost = queryMemberByIdService.findById(participantId);

        study.delegateStudyHostAuthority(newHost);
        participantRepository.deleteParticipantAssociatedMember(study, newHost);
    }

    @StudyWithMeWritableTransactional
    public void graduate(
            final Long studyId,
            final Long participantId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member participant = queryMemberByIdService.findById(participantId);
        validateGraduationRequirements(study, participantId);

        study.graduateParticipant(participant);

        if (participant.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyGraduatedEvent(
                            participant.getEmailValue(),
                            participant.getNicknameValue(),
                            study.getNameValue()
                    )
            );
        }
    }

    private void validateGraduationRequirements(
            final Study study,
            final Long memberId
    ) {
        final int attendanceCount
                = memberRepository.getAttendanceCount(study.getId(), memberId, ATTENDANCE).intValue();

        if (!study.isGraduationRequirementsFulfilled(attendanceCount)) {
            throw StudyWithMeException.type(StudyErrorCode.GRADUATION_REQUIREMENTS_NOT_FULFILLED);
        }
    }
}
