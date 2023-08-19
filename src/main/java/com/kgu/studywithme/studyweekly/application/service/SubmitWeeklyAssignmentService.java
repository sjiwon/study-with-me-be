package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantReadAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.event.AssignmentSubmittedEvent;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class SubmitWeeklyAssignmentService implements SubmitWeeklyAssignmentUseCase {
    private final StudyWeeklyJpaRepository studyWeeklyJpaRepository;
    private final ParticipantReadAdapter participantReadAdapter;
    private final FileUploader uploader;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        validateAssignmentSubmissionExists(command.file(), command.link());

        final StudyWeekly weekly = getSpecificWeekly(command.weeklyId());
        final Member member = participantReadAdapter.getParticipant(command.studyId(), command.memberId());

        final UploadAssignment assignment = uploadAssignment(command.submitType(), command.file(), command.link());
        weekly.submitAssignment(member.getId(), assignment);

        eventPublisher.publishEvent(new AssignmentSubmittedEvent(command.studyId(), weekly.getId(), member.getId()));
    }

    private void validateAssignmentSubmissionExists(
            final RawFileData file,
            final String link
    ) {
        if (file == null && link == null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.MISSING_SUBMISSION);
        }

        if (file != null && link != null) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION);
        }
    }

    private StudyWeekly getSpecificWeekly(final Long weeklyId) {
        return studyWeeklyJpaRepository.findById(weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }

    private UploadAssignment uploadAssignment(
            final AssignmentSubmitType submitType,
            final RawFileData file,
            final String link
    ) {
        return submitType == FILE
                ? UploadAssignment.withFile(file.originalFileName(), uploader.uploadWeeklySubmit(file))
                : UploadAssignment.withLink(link);
    }
}
