package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.persistence.StudyAttendanceJpaRepository;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import com.kgu.studywithme.studyweekly.application.adapter.StudyWeeklyHandlingRepositoryAdapter;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.infrastructure.persistence.StudyWeeklyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class CreateStudyWeeklyService implements CreateStudyWeeklyUseCase {
    private final FileUploader uploader;
    private final StudyWeeklyHandlingRepositoryAdapter studyWeeklyHandlingRepositoryAdapter;
    private final StudyWeeklyJpaRepository studyWeeklyJpaRepository;
    private final StudyParticipantJpaRepository studyParticipantJpaRepository;
    private final StudyAttendanceJpaRepository studyAttendanceJpaRepository;

    @Override
    public Long invoke(final Command command) {
        final List<UploadAttachment> attachments = uploadAttachments(command.files());
        final int nextWeek = studyWeeklyHandlingRepositoryAdapter.getNextWeek(command.studyId());

        final StudyWeekly weekly = studyWeeklyJpaRepository.save(createWeekly(command, attachments, nextWeek));
        applyParticipantsAttendanceToNextWeek(weekly);
        return weekly.getId();
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

    private StudyWeekly createWeekly(
            final Command command,
            final List<UploadAttachment> attachments,
            final int nextWeek
    ) {
        if (command.assignmentExists()) {
            return StudyWeekly.createWeeklyWithAssignment(
                    command.studyId(),
                    command.creatorId(),
                    command.title(),
                    command.content(),
                    nextWeek,
                    command.period(),
                    command.autoAttendance(),
                    attachments
            );
        }

        return StudyWeekly.createWeekly(
                command.studyId(),
                command.creatorId(),
                command.title(),
                command.content(),
                nextWeek,
                command.period(),
                attachments
        );
    }

    private void applyParticipantsAttendanceToNextWeek(final StudyWeekly weekly) {
        final List<StudyAttendance> participantsAttendance = new ArrayList<>();
        final List<Long> studyParticipantIds = studyParticipantJpaRepository.findStudyParticipantIds(weekly.getStudyId());
        studyParticipantIds.forEach(
                studyParticipantId -> participantsAttendance.add(
                        StudyAttendance.recordAttendance(
                                weekly.getStudyId(),
                                studyParticipantId,
                                weekly.getWeek(),
                                NON_ATTENDANCE
                        )
                )
        );
        studyAttendanceJpaRepository.saveAll(participantsAttendance);
    }
}
