package com.kgu.studywithme.studyparticipant.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyParticipantErrorCode implements ErrorCode {
    STUDY_IS_NOT_RECRUITING_NOW(HttpStatus.CONFLICT, "STUDY_PARTICIPANT_001", "현재 스터디원들을 모집하고 있지 않는 스터디입니다."),
    STUDY_HOST_CANNOT_APPLY(HttpStatus.FORBIDDEN, "STUDY_PARTICIPANT_002", "스터디 팀장은 본인 스터디에 참여 신청을 할 수 없습니다."),
    ALREADY_APPLY_OR_PARTICIPATE(HttpStatus.BAD_REQUEST, "STUDY_PARTICIPANT_003", "이미 신청했거나 참여중입니다."),
    ALREADY_CANCEL_OR_GRADUATED(HttpStatus.BAD_REQUEST, "STUDY_PARTICIPANT_004", "참여 취소 또는 졸업한 스터디는 다시 참여 신청할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
