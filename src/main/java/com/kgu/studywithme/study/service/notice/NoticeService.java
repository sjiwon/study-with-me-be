package com.kgu.studywithme.study.service.notice;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.NoticeRepository;
import com.kgu.studywithme.study.domain.notice.comment.CommentRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.service.StudyFindService;
import com.kgu.studywithme.study.service.StudyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final CommentRepository commentRepository;
    private final StudyFindService studyFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public Long register(
            final Long studyId,
            final String title,
            final String content
    ) {
        final Study study = studyFindService.findByIdWithHost(studyId);
        final Notice notice = Notice.writeNotice(study, title, content);

        return noticeRepository.save(notice).getId();
    }

    @Transactional
    public void remove(
            final Long noticeId,
            final Long hostId
    ) {
        validateNoticeWriter(noticeId, hostId);

        commentRepository.deleteByNoticeId(noticeId);
        noticeRepository.deleteById(noticeId);
    }

    @Transactional
    public void update(
            final Long noticeId,
            final Long hostId,
            final String title,
            final String content
    ) {
        validateNoticeWriter(noticeId, hostId);

        final Notice notice = findById(noticeId);
        notice.updateNoticeInformation(title, content);
    }

    private void validateNoticeWriter(
            final Long noticeId,
            final Long memberId
    ) {
        studyValidator.validateNoticeWriter(noticeId, memberId);
    }

    private Notice findById(final Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.NOTICE_NOT_FOUND));
    }
}
