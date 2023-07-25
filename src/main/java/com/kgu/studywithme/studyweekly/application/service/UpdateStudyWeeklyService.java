package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import com.kgu.studywithme.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyWeeklyService implements UpdateStudyWeeklyUseCase {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final FileUploader uploader;

    @Override
    public void updateStudyWeekly(final Command command) {
        final StudyWeekly weekly = getSpecificWeekly(command.studyId(), command.week());
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

    private StudyWeekly getSpecificWeekly(final Long studyId, final int week) {
        return studyWeeklyRepository.getSpecificWeekly(studyId, week)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }

    private List<UploadAttachment> uploadAttachments(final List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        return files.stream()
                .map(file ->
                        new UploadAttachment(
                                file.getOriginalFilename(),
                                uploader.uploadWeeklyAttachment(file)
                        )
                )
                .toList();
    }
}
