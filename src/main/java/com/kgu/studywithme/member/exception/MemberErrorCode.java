package com.kgu.studywithme.member.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_001", "닉네임 형식에 맞지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_002", "이미 사용중인 닉네임입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_003", "구글 이메일 형식에 맞지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_004", "이미 사용중인 이메일입니다."),
    REGION_IS_BLANK(HttpStatus.BAD_REQUEST, "MEMBER_005", "거주지를 정확하게 입력해주세요."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "MEMBER_006", "이미 사용중인 전화번호입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_007", "사용자 정보를 찾을 수 없습니다."),
    MEMBER_IS_NOT_WRITER(HttpStatus.CONFLICT, "MEMBER_008", "작성자가 아닙니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "MEMBER_014", "유효하지 않은 성별입니다."),
    NICKNAME_SAME_AS_BEFORE(HttpStatus.CONFLICT, "MEMBER_015", "이전과 동일한 닉네임으로 수정할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
