package com.kgu.studywithme.member.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_NICKNAME_FORMAT(BAD_REQUEST, "MEMBER_001", "닉네임 형식에 맞지 않습니다."),
    NICKNAME_SAME_AS_BEFORE(CONFLICT, "MEMBER_002", "이전과 동일한 닉네임으로 수정할 수 없습니다."),
    INVALID_EMAIL_FORMAT(BAD_REQUEST, "MEMBER_003", "구글 이메일 형식에 맞지 않습니다."),
    INVALID_GENDER(BAD_REQUEST, "MEMBER_004", "유효하지 않은 성별입니다."),
    REGION_IS_BLANK(BAD_REQUEST, "MEMBER_005", "거주지를 정확하게 입력해주세요."),
    INTEREST_MUST_EXISTS_AT_LEAST_ONE(BAD_REQUEST, "MEMBER_006", "관심사는 적어도 1개 이상 등록해야 합니다."),
    DUPLICATE_EMAIL(CONFLICT, "MEMBER_007", "이미 사용중인 이메일입니다."),
    DUPLICATE_NICKNAME(CONFLICT, "MEMBER_008", "이미 사용중인 닉네임입니다."),
    DUPLICATE_PHONE(CONFLICT, "MEMBER_009", "이미 사용중인 전화번호입니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_010", "사용자 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
