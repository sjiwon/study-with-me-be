package com.kgu.studywithme.favorite.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements ErrorCode {
    ALREADY_LIKE_MARKED(CONFLICT, "FAVORITE_001", "이미 찜한 스터디입니다."),
    NEVER_LIKE_MARKED(CONFLICT, "FAVORITE_002", "찜 등록되어 있지 않은 스터디입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
