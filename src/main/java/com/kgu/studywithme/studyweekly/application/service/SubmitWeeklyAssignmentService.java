package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.event.AssignmentSubmittedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class SubmitWeeklyAssignmentService implements SubmitWeeklyAssignmentUseCase {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final ParticipateMemberReader participateMemberReader;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        final StudyWeekly weekly = getSpecificWeekly(command.weeklyId());
        final Member member = participateMemberReader.getParticipant(command.studyId(), command.memberId());
        weekly.submitAssignment(member.getId(), command.assignment());

        eventPublisher.publishEvent(new AssignmentSubmittedEvent(command.studyId(), weekly.getId(), member.getId()));
    }

    private StudyWeekly getSpecificWeekly(final Long weeklyId) {
        return studyWeeklyRepository.findById(weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }
}
