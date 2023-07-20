package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.Period;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CreateStudyWeeklyUseCase {
    void createStudyWeekly(final Command command);

    record Command(
            Long studyId,
            Long creatorId,
            String title,
            String content,
            Period period,
            boolean assignmentExists,
            boolean autoAttendance,
            List<MultipartFile> files
    ) {
    }
}
