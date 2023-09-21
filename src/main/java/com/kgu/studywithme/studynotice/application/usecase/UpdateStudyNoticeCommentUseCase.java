package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyNoticeCommentUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;

    public void invoke(final UpdateStudyNoticeCommentCommand command) {
        final StudyNoticeComment comment = studyNoticeCommentRepository.getById(command.commentId());
        validateCommentWriter(comment, command.memberId());

        comment.updateComment(command.content());
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Long memberId) {
        if (!comment.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT);
        }
    }
}
