package com.kgu.studywithme.studyattendance.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyAttendanceErrorCode implements ErrorCode {
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_ATTENDANCE_001", "출석 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
