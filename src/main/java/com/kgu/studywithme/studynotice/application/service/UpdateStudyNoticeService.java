package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.studynotice.application.adapter.StudyNoticeHandlingRepositoryAdapter;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateStudyNoticeService implements UpdateStudyNoticeUseCase {
    private final StudyNoticeHandlingRepositoryAdapter studyNoticeHandlingRepositoryAdapter;

    @Override
    public void invoke(final Command command) {
        studyNoticeHandlingRepositoryAdapter.updateNotice(command.noticeId(), command.title(), command.content());
    }
}
