package com.kgu.studywithme.studyweekly.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyErrorCode implements ErrorCode {
    // StudyWeekly
    WEEKLY_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_WEEKLY_001", "해당 주차 정보를 찾을 수 없습니다."),
    ONLY_LATEST_WEEKLY_CAN_DELETE(HttpStatus.CONFLICT, "STUDY_WEEKLY_002", "가장 최신 주차만 삭제할 수 있습니다."),

    // StudyWeeklySubmit
    MISSING_SUBMISSION(HttpStatus.BAD_REQUEST, "STUDY_WEEKLY_SUBMIT_001", "과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 합니다."),
    DUPLICATE_SUBMISSION(HttpStatus.BAD_REQUEST, "STUDY_WEEKLY_SUBMIT_002", "과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 합니다."),
    SUBMITTED_ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_WEEKLY_SUBMIT_003", "제출한 과제가 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
