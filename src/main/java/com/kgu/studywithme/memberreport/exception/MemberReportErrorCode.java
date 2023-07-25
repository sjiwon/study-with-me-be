package com.kgu.studywithme.memberreport.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@RequiredArgsConstructor
public enum MemberReportErrorCode implements ErrorCode {
    PREVIOUS_REPORT_IS_STILL_PENDING(CONFLICT, "MEMBER_REPORT_001", "해당 사용자에 대해서 이전에 신고하신 내역이 처리중입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
