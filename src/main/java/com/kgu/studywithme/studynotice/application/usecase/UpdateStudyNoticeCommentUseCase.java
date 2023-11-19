package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeCommentRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateStudyNoticeCommentUseCase {
    private final StudyNoticeCommentRepository studyNoticeCommentRepository;
    private final MemberRepository memberRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final UpdateStudyNoticeCommentCommand command) {
        final StudyNoticeComment comment = studyNoticeCommentRepository.getById(command.commentId());
        final Member member = memberRepository.getById(command.memberId());

        validateCommentWriter(comment, member);
        comment.updateComment(command.content());
    }

    private void validateCommentWriter(final StudyNoticeComment comment, final Member member) {
        if (!comment.isWriter(member)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT);
        }
    }
}
