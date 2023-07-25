package com.kgu.studywithme.studyparticipant.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum StudyParticipantErrorCode implements ErrorCode {
    STUDY_IS_NOT_RECRUITING_NOW(CONFLICT, "STUDY_PARTICIPANT_001", "현재 스터디원들을 모집하고 있지 않는 스터디입니다."),
    STUDY_HOST_CANNOT_APPLY(FORBIDDEN, "STUDY_PARTICIPANT_002", "스터디 팀장은 본인 스터디에 참여 신청을 할 수 없습니다."),
    ALREADY_APPLY_OR_PARTICIPATE(CONFLICT, "STUDY_PARTICIPANT_003", "이미 신청했거나 참여중입니다."),
    ALREADY_LEAVE_OR_GRADUATED(CONFLICT, "STUDY_PARTICIPANT_004", "참여 취소 또는 졸업한 스터디는 다시 참여 신청할 수 없습니다."),
    APPLIER_NOT_FOUND(NOT_FOUND, "STUDY_PARTICIPANT_005", "신청자 정보를 찾을 수 없습니다."),
    STUDY_IS_TERMINATED(CONFLICT, "STUDY_PARTICIPANT_006", "스터디가 종료되었습니다."),
    STUDY_CAPACITY_ALREADY_FULL(CONFLICT, "STUDY_PARTICIPANT_007", "스터디 정원이 꽉 찼습니다."),
    SELF_DELEGATING_NOT_ALLOWED(CONFLICT, "STUDY_PARTICIPANT_008", "새로운 스터디 팀장은 자기자신이 아니라 다른 참여자를 선택해주세요."),
    NON_PARTICIPANT_CANNOT_BE_HOST(CONFLICT, "STUDY_PARTICIPANT_009", "새로운 스터디 팀장은 스터디 참여자 중 한명을 선택해주세요."),
    HOST_CANNOT_LEAVE_STUDY(CONFLICT, "STUDY_PARTICIPANT_010", "스터디 팀장은 팀장 권한을 위임하고 스터디에서 떠날 수 있습니다."),
    HOST_CANNOT_GRADUATE_STUDY(CONFLICT, "STUDY_PARTICIPANT_011", "스터디 팀장은 팀장 권한을 위임하고 스터디를 졸업할 수 있습니다."),
    PARTICIPANT_NOT_FOUND(NOT_FOUND, "STUDY_PARTICIPANT_012", "참여자 정보를 찾을 수 없습니다."),
    PARTICIPANT_NOT_MEET_GRADUATION_POLICY(CONFLICT, "STUDY_PARTICIPANT_013", "졸업 요건을 채우지 않으면 스터디 졸업을 할 수 없습니다."),
    MEMBER_IS_NOT_PARTICIPANT(FORBIDDEN, "STUDY_PARTICIPANT_014", "스터디 참여자가 아니면 권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
