package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import com.kgu.studywithme.study.event.StudyApprovedEvent;
import com.kgu.studywithme.study.event.StudyGraduatedEvent;
import com.kgu.studywithme.study.event.StudyRejectedEvent;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationService {
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void apply(
            final Long studyId,
            final Long memberId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member member = memberFindService.findById(memberId);

        study.applyParticipation(member);
    }

    @Transactional
    public void applyCancel(
            final Long studyId,
            final Long applierId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member applier = memberFindService.findById(applierId);
        study.validateMemberIsApplier(applier);

        participantRepository.deleteParticipantAssociatedMember(study, applier);
    }

    @Transactional
    public void approve(
            final Long studyId,
            final Long applierId,
            final Long hostId
    ) {
        final Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        final Member applier = memberFindService.findById(applierId);

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

    @Transactional
    public void reject(
            final Long studyId,
            final Long applierId,
            final Long hostId,
            final String reason
    ) {
        final Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        final Member applier = memberFindService.findById(applierId);

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

    @Transactional
    public void cancel(
            final Long studyId,
            final Long participantId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member participant = memberFindService.findById(participantId);

        study.cancelParticipation(participant);
    }

    @Transactional
    public void delegateAuthority(
            final Long studyId,
            final Long participantId,
            final Long hostId
    ) {
        final Study study = studyFindService.findByIdAndHostIdWithParticipants(studyId, hostId);
        final Member newHost = memberFindService.findById(participantId);

        study.delegateStudyHostAuthority(newHost);
        participantRepository.deleteParticipantAssociatedMember(study, newHost);
    }

    @Transactional
    public void graduate(
            final Long studyId,
            final Long participantId
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member participant = memberFindService.findById(participantId);
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
