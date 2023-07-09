package com.kgu.studywithme.study.service.notice;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.MemberFindService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeCommentService {
    private final NoticeRepository noticeRepository;
    private final CommentRepository commentRepository;
    private final MemberFindService memberFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public void register(
            final Long noticeId,
            final Long memberId,
            final String content
    ) {
        final Notice notice = findNoticeById(noticeId);
        final Member writer = memberFindService.findById(memberId);
        validateWriterIsParticipant(notice, writer);

        notice.addComment(writer, content);
    }

    public Notice findNoticeById(final Long noticeId) {
        return noticeRepository.findByIdWithStudy(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.NOTICE_NOT_FOUND));
    }

    private void validateWriterIsParticipant(
            final Notice notice,
            final Member writer
    ) {
        final Study study = notice.getStudy();
        study.validateMemberIsParticipant(writer);
    }

    @Transactional
    public void remove(
            final Long commentId,
            final Long memberId
    ) {
        validateCommentWriter(commentId, memberId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void update(
            final Long commentId,
            final Long memberId,
            final String content
    ) {
        validateCommentWriter(commentId, memberId);

        final Comment comment = findCommentById(commentId);
        comment.updateComment(content);
    }

    private Comment findCommentById(final Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentWriter(
            final Long commentId,
            final Long memberId
    ) {
        studyValidator.validateCommentWriter(commentId, memberId);
    }
}
