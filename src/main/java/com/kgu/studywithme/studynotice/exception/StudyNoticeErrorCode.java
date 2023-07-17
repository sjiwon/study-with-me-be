package com.kgu.studywithme.studynotice.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StudyNoticeErrorCode implements ErrorCode {
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDY_NOTICE_001", "공지사항이 존재하지 않습니다."),
    ONLY_WRITER_CAN_UPDATE_NOTICE(HttpStatus.FORBIDDEN, "STUDY_NOTICE_002", "공지사항은 작성자만 변경할 수 있습니다."),
    ONLY_WRITER_CAN_DELETE_NOTICE(HttpStatus.FORBIDDEN, "STUDY_NOTICE_003", "공지사항은 작성자만 삭제할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
