package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import com.kgu.studywithme.studynotice.infrastructure.persistence.comment.StudyNoticeCommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyNoticeCommentService implements DeleteStudyNoticeCommentUseCase {
    private final StudyNoticeCommentJpaRepository studyNoticeCommentJpaRepository;

    @Override
    public void invoke(final Command command) {
        final StudyNoticeComment comment = findById(command.commentId());
        validateCommentWriter(comment, command.memberId());

        studyNoticeCommentJpaRepository.delete(comment);
    }

    private StudyNoticeComment findById(final Long commentId) {
        return studyNoticeCommentJpaRepository.findById(commentId)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_COMMENT_NOT_FOUND));
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Long memberId) {
        if (!comment.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT);
        }
    }
}
