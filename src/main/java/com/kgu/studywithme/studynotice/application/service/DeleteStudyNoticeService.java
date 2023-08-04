package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class DeleteStudyNoticeService implements DeleteStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    @Override
    public void deleteNotice(final Command command) {
        studyNoticeRepository.deleteNotice(command.noticeId());
    }
}
