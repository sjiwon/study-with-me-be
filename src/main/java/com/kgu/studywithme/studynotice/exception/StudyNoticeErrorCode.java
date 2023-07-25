package com.kgu.studywithme.studynotice.exception;

import com.kgu.studywithme.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum StudyNoticeErrorCode implements ErrorCode {
    NOTICE_NOT_FOUND(NOT_FOUND, "STUDY_NOTICE_001", "공지사항이 존재하지 않습니다."),
    ONLY_WRITER_CAN_UPDATE_NOTICE(FORBIDDEN, "STUDY_NOTICE_002", "공지사항은 작성자만 변경할 수 있습니다."),
    ONLY_WRITER_CAN_DELETE_NOTICE(FORBIDDEN, "STUDY_NOTICE_003", "공지사항은 작성자만 삭제할 수 있습니다."),
    ONLY_PARTICIPANT_CAN_WRITE_COMMENT(FORBIDDEN, "STUDY_NOTICE_004", "스터디 참여자만 공지사항에 댓글을 작성할 수 있습니다."),
    NOTICE_COMMENT_NOT_FOUND(NOT_FOUND, "STUDY_NOTICE_005", "작성하신 댓글이 존재하지 않습니다."),
    ONLY_WRITER_CAN_UPDATE_NOTICE_COMMENT(FORBIDDEN, "STUDY_NOTICE_006", "공지사항 댓글은 작성자만 변경할 수 있습니다."),
    ONLY_WRITER_CAN_DELETE_NOTICE_COMMENT(FORBIDDEN, "STUDY_NOTICE_007", "공지사항 댓글은 작성자만 삭제할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
