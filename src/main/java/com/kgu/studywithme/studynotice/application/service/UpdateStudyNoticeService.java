package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyNoticeService implements UpdateStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    @Override
    public void updateNotice(final Command command) {
        studyNoticeRepository.updateNotice(command.noticeId(), command.title(), command.content());
    }
}
