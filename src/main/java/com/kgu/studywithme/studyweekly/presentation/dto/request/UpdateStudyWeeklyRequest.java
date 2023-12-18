package com.kgu.studywithme.studyweekly.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateStudyWeeklyRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @NotNull(message = "주차 시작날짜는 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startDate,

        @NotNull(message = "주차 종료날짜는 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endDate,

        @NotNull(message = "과제 여부는 필수입니다.")
        Boolean assignmentExists,

        @NotNull(message = "자동 출석 여부는 필수입니다.")
        Boolean autoAttendance,

        @Size(max = 10, message = "첨부파일은 최대 10개까지 등록할 수 있습니다.")
        List<MultipartFile> files
) {
}
