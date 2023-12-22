package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeleteStudyNoticeCommentUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;
    private final MemberRepository memberRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final DeleteStudyNoticeCommentCommand command) {
        final StudyNoticeComment comment = studyNoticeCommentRepository.getByIdWithWriter(command.commentId());
        final Member member = memberRepository.getById(command.memberId());

        validateCommentWriter(comment, member);
        studyNoticeCommentRepository.delete(comment);
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Member member) {
        if (!comment.isWriter(member)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT);
        }
    }
}
