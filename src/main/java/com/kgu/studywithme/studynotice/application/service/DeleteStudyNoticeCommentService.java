package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyNoticeCommentService implements DeleteStudyNoticeCommentUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;

    @Override
    public void invoke(final Command command) {
        final StudyNoticeComment comment = findById(command.commentId());
        validateCommentWriter(comment, command.memberId());

        studyNoticeCommentRepository.delete(comment);
    }

    private StudyNoticeComment findById(final Long commentId) {
        return studyNoticeCommentRepository.findById(commentId)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_COMMENT_NOT_FOUND));
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Long memberId) {
        if (!comment.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT);
        }
    }
}
