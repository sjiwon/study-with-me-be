package com.kgu.studywithme.category.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_EXIST(NOT_FOUND, "CATEGORY_001", "존재하지 않는 카테고리입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
