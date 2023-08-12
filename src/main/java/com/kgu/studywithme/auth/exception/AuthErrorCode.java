package com.kgu.studywithme.auth.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    EXPIRED_TOKEN(UNAUTHORIZED, "AUTH_001", "토큰의 유효기간이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_002", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(FORBIDDEN, "AUTH_003", "권한이 없습니다."),
    INVALID_OAUTH_PROVIDER(BAD_REQUEST, "AUTH_004", "제공하지 않는 OAuth Provider입니다."),
    GOOGLE_OAUTH_EXCEPTION(INTERNAL_SERVER_ERROR, "AUTH_005", "Google 서버 요청 간 에러가 발생하였습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
