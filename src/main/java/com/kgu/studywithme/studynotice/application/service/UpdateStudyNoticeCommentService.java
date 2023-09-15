package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyNoticeCommentService implements UpdateStudyNoticeCommentUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;

    @Override
    public void invoke(final Command command) {
        final StudyNoticeComment comment = findById(command.commentId());
        validateCommentWriter(comment, command.memberId());

        comment.updateComment(command.content());
    }

    private StudyNoticeComment findById(final Long commentId) {
        return studyNoticeCommentRepository.findById(commentId)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_COMMENT_NOT_FOUND));
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Long memberId) {
        if (!comment.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT);
        }
    }
}
