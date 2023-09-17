package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class WriteStudyNoticeCommentUseCase {
    private final StudyNoticeRepository studyNoticeRepository;
    private final ParticipantVerificationRepositoryAdapter participantVerificationRepositoryAdapter;

    public void invoke(final WriteStudyNoticeCommentCommand command) {
        final StudyNotice notice = studyNoticeRepository.getById(command.noticeId());
        validateWriterIsStudyParticipant(notice.getStudyId(), command.writerId());

        notice.addComment(command.writerId(), command.content());
    }

    private void validateWriterIsStudyParticipant(final Long studyId, final Long writerId) {
        if (!participantVerificationRepositoryAdapter.isParticipant(studyId, writerId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT);
        }
    }
}
