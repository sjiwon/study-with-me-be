package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyNoticeUseCase {
    private final StudyNoticeRepository studyNoticeRepository;

    public Long invoke(final WriteStudyNoticeCommand command) {
        return studyNoticeRepository.save(command.toDomain()).getId();
    }
}
