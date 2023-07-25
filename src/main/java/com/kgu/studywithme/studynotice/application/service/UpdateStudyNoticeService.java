package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyNoticeService implements UpdateStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    @Override
    public void updateNotice(final Command command) {
        final StudyNotice notice = findById(command.noticeId());
        validateNoticeWriter(notice, command.hostId());

        notice.updateNoticeInformation(command.title(), command.content());
    }

    private StudyNotice findById(final Long noticeId) {
        return studyNoticeRepository.findById(noticeId)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_NOT_FOUND));
    }

    private void validateNoticeWriter(
            final StudyNotice notice,
            final Long hostId
    ) {
        if (!notice.isWriter(hostId)) {
            throw StudyWithMeException.type(StudyNoticeErrorCode.ONLY_WRITER_CAN_UPDATE_NOTICE);
        }
    }
}
