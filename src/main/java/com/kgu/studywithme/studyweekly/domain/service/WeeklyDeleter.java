package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyAttachmentRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeeklyDeleter {
    private final StudyWeeklyRepository studyWeeklyRepository;
    private final StudyAttendanceRepository studyAttendanceRepository;
    private final StudyWeeklyAttachmentRepository studyWeeklyAttachmentRepository;
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final StudyWeekly weekly) {
        // 1. 해당 주차 출석 정보 삭제
        studyAttendanceRepository.deleteFromSpecificWeekly(weekly.getStudy().getId(), weekly.getWeek());

        // 2. 제출한 과제 삭제
        studyWeeklySubmitRepository.deleteFromSpecificWeekly(weekly.getId());

        // 3. 해당 주차 첨부파일 삭제
        studyWeeklyAttachmentRepository.deleteFromSpecificWeekly(weekly.getId());

        // 4. 해당 주차 삭제
        studyWeeklyRepository.deleteById(weekly.getId());
    }
}
