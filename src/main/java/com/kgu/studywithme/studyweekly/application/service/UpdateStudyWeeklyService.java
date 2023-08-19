package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyWeeklyService implements UpdateStudyWeeklyUseCase {
    private final StudyWeeklyJpaRepository studyWeeklyJpaRepository;
    private final FileUploader uploader;

    @Override
    public void invoke(final Command command) {
        final StudyWeekly weekly = getSpecificWeekly(command.weeklyId());
        final List<UploadAttachment> attachments = uploadAttachments(command.files());

        weekly.update(
                command.title(),
                command.content(),
                command.period(),
                command.assignmentExists(),
                command.autoAttendance(),
                attachments
        );
    }

    private StudyWeekly getSpecificWeekly(final Long weeklyId) {
        return studyWeeklyJpaRepository.findById(weeklyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }

    private List<UploadAttachment> uploadAttachments(final List<RawFileData> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        return files.stream()
                .map(file ->
                        new UploadAttachment(
                                file.originalFileName(),
                                uploader.uploadWeeklyAttachment(file)
                        )
                )
                .toList();
    }
}
