package com.kgu.studywithme.studyreview.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum StudyReviewErrorCode implements ErrorCode {
    ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW(FORBIDDEN, "STUDY_REVIEW_001", "졸업한 사람만 리뷰를 작성할 수 있습니다."),
    ALREADY_WRITTEN(CONFLICT, "STUDY_REVIEW_002", "이미 리뷰를 작성했습니다."),
    STUDY_REVIEW_NOT_FOUND(NOT_FOUND, "STUDY_REVIEW_003", "작성하신 리뷰가 존재하지 않습니다."),
    ONLY_WRITER_CAN_UPDATE(FORBIDDEN, "STUDY_REVIEW_004", "리뷰는 작성자만 변경할 수 있습니다."),
    ONLY_WRITER_CAN_DELETE(FORBIDDEN, "STUDY_REVIEW_005", "리뷰는 작성자만 삭제할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
