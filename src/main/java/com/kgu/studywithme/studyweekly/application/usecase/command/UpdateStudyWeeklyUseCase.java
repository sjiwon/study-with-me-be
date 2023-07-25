package com.kgu.studywithme.studyweekly.application.usecase.command;

import com.kgu.studywithme.studyweekly.domain.Period;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UpdateStudyWeeklyUseCase {
    void updateStudyWeekly(final Command command);

    record Command(
            Long studyId,
            int week,
            String title,
            String content,
            Period period,
            boolean assignmentExists,
            boolean autoAttendance,
            List<MultipartFile> files
    ) {
    }
}
