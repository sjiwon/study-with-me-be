package com.kgu.studywithme.auth.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_001", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(FORBIDDEN, "AUTH_002", "권한이 없습니다."),
    INVALID_OAUTH_PROVIDER(BAD_REQUEST, "AUTH_003", "제공하지 않는 OAuth Provider입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
