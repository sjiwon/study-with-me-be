package com.kgu.studywithme.studynotice.application.service;

import com.kgu.studywithme.studynotice.application.adapter.StudyNoticeHandlingRepositoryAdapter;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyNoticeService implements DeleteStudyNoticeUseCase {
    private final StudyNoticeHandlingRepositoryAdapter studyNoticeHandlingRepositoryAdapter;

    @Override
    public void invoke(final Command command) {
        studyNoticeHandlingRepositoryAdapter.deleteNotice(command.noticeId());
    }
}
