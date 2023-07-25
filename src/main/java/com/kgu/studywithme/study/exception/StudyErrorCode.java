package com.kgu.studywithme.study.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum StudyErrorCode implements ErrorCode {
    NAME_IS_BLANK(BAD_REQUEST, "STUDY_001", "스터디 이름은 공백을 허용하지 않습니다."),
    NAME_LENGTH_IS_OUT_OF_RANGE(BAD_REQUEST, "STUDY_002", "스터디 이름은 최대 20자까지 가능합니다."),
    DESCRIPTION_IS_BLANK(BAD_REQUEST, "STUDY_003", "스터디 설명은 공백을 허용하지 않습니다."),
    CAPACITY_IS_OUT_OF_RANGE(BAD_REQUEST, "STUDY_004", "스터디 인원은 2명 이상 10명 이하여야 합니다."),
    STUDY_THUMBNAIL_NOT_FOUND(NOT_FOUND, "STUDY_005", "제공해주지 않는 썸네일입니다."),
    STUDY_TYPE_IS_WEIRD(BAD_REQUEST, "STUDY_006", "스터디 유형은 온라인/오프라인 중 하나를 선택해주세요."),
    STUDY_LOCATION_IS_BLANK(BAD_REQUEST, "STUDY_007", "오프라인으로 진행되는 스터디는 장소가 필수입니다."),
    NO_CHANCE_TO_UPDATE_GRADUATION_POLICY(CONFLICT, "STUDY_008", "졸업 요건을 수정할 기회가 남아있지 않습니다."),
    HASHTAG_MUST_EXISTS_AT_LEAST_ONE(BAD_REQUEST, "STUDY_009", "스터디 해시태그는 적어도 1개 이상 존재해야 합니다."),
    HASHTAG_MUST_NOT_EXISTS_MORE_THAN_FIVE(BAD_REQUEST, "STUDY_010", "스터디 해시태그는 5개보다 많으면 안됩니다."),
    STUDY_NOT_FOUND(NOT_FOUND, "STUDY_011", "스터디가 존재하지 않습니다."),
    DUPLICATE_NAME(CONFLICT, "STUDY_012", "이미 사용중인 이름입니다."),
    CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS(CONFLICT, "STUDY_013", "현재 참여자 수보다 낮게 스터디 정원을 적용할 수 없습니다."),
    INVALID_SORT_TYPE(BAD_REQUEST, "STUDY_014", "제공하지 않는 검색 조건입니다."),
    MEMBER_IS_NOT_HOST(FORBIDDEN, "STUDY_015", "스터디 팀장이 아니면 권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
