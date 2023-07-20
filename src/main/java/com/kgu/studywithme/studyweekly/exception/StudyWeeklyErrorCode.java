package com.kgu.studywithme.studyweekly.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyErrorCode implements ErrorCode {
    WEEKLY_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_WEEKLY_001", "해당 주차 정보를 찾을 수 없습니다."),
    ONLY_LATEST_WEEKLY_CAN_DELETE(HttpStatus.CONFLICT, "STUDY_WEEKLY_002", "가장 최신 주차만 삭제할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
