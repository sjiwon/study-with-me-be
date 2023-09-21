package com.kgu.studywithme.studyweekly.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyErrorCode implements ErrorCode {
    // StudyWeekly
    WEEKLY_NOT_FOUND(NOT_FOUND, "STUDY_WEEKLY_001", "해당 주차 정보를 찾을 수 없습니다."),
    ONLY_LATEST_WEEKLY_CAN_DELETE(CONFLICT, "STUDY_WEEKLY_002", "가장 최신 주차만 삭제할 수 있습니다."),
    PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE(BAD_REQUEST, "STUDY_WEEKLY_003", "시작일은 종료일 이전이어야 합니다."),

    // StudyWeeklySubmit
    INVALID_SUBMIT_TYPE(BAD_REQUEST, "STUDY_WEEKLY_SUBMIT_001", "과제 제출 타입은 링크(link), 파일(file) 중 하나입니다."),
    MISSING_SUBMISSION(BAD_REQUEST, "STUDY_WEEKLY_SUBMIT_002", "과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 합니다."),
    DUPLICATE_SUBMISSION(BAD_REQUEST, "STUDY_WEEKLY_SUBMIT_003", "과제 제출물은 링크 또는 파일 중 하나만 업로드해야 합니다."),
    INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT(BAD_REQUEST, "STUDY_WEEKLY_SUBMIT_004", "과제 타입[link/file]과 제출한 파일[링크/파일]이 매치되지 않습니다"),
    SUBMITTED_ASSIGNMENT_NOT_FOUND(NOT_FOUND, "STUDY_WEEKLY_SUBMIT_005", "제출한 과제가 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
