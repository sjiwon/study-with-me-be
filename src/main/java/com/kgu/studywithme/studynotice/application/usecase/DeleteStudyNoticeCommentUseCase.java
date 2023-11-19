package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyNoticeCommentUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final DeleteStudyNoticeCommentCommand command) {
        final StudyNoticeComment comment = studyNoticeCommentRepository.getById(command.commentId());
        validateCommentWriter(comment, command.memberId());

        studyNoticeCommentRepository.delete(comment);
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Long memberId) {
        if (!comment.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT);
        }
    }
}
