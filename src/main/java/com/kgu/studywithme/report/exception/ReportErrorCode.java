package com.kgu.studywithme.report.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    PREVIOUS_REPORT_IS_STILL_PENDING(HttpStatus.CONFLICT, "REPORT_001", "해당 사용자에 대해서 이전에 신고하신 내역이 처리중입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
