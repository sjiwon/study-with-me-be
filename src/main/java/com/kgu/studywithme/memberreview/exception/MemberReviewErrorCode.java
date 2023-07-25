package com.kgu.studywithme.memberreview.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum MemberReviewErrorCode implements ErrorCode {
    SELF_REVIEW_NOT_ALLOWED(CONFLICT, "MEMBER_REVIEW_001", "본인에게 리뷰를 남길 수 없습니다."),
    COMMON_STUDY_RECORD_NOT_FOUND(CONFLICT, "MEMBER_REVIEW_002", "함께 스터디를 진행한 기록이 없으면 리뷰를 작성할 수 없습니다."),
    ALREADY_REVIEW(CONFLICT, "MEMBER_REVIEW_003", "이미 해당 사용자에게 리뷰를 작성했습니다."),
    MEMBER_REVIEW_NOT_FOUND(NOT_FOUND, "MEMBER_REVIEW_004", "작성한 리뷰를 찾을 수 없습니다."),
    CONTENT_SAME_AS_BEFORE(CONFLICT, "MEMBER_REVIEW_005", "이전과 동일한 내용으로 리뷰를 수정할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
