package com.kgu.studywithme.peerreview.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PeerReviewErrorCode implements ErrorCode {
    SELF_REVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PEER_REVIEW_001", "본인에게 피어리뷰를 남길 수 없습니다."),
    COMMON_STUDY_RECORD_NOT_FOUND(HttpStatus.CONFLICT, "PEER_REVIEW_002", "함께 스터디를 진행한 기록이 없으면 피어리뷰를 작성할 수 없습니다."),
    ALREADY_REVIEW(HttpStatus.CONFLICT, "PEER_REVIEW_003", "이미 해당 사용자에게 리뷰를 작성했습니다."),
    PEER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "PEER_REVIEW_004", "작성한 피어리뷰를 찾을 수 없습니다."),
    CONTENT_SAME_AS_BEFORE(HttpStatus.CONFLICT, "PEER_REVIEW_005", "이전과 동일한 내용으로 리뷰를 수정할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
