package com.kgu.studywithme.global.exception.dto;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
public class ErrorResponse {
    private int status;
    private String errorCode;
    private String message;

    private ErrorResponse(final ErrorCode code) {
        this.status = code.getStatus().value();
        this.errorCode = code.getErrorCode();
        this.message = code.getMessage();
    }

    public static ErrorResponse from(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(final ErrorCode errorCode, final String message) {
        return new ErrorResponse(errorCode.getStatus().value(), errorCode.getErrorCode(), message);
    }
}
