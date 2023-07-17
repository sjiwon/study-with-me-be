package com.kgu.studywithme.studynotice.domain.comment;

import com.kgu.studywithme.studynotice.domain.StudyNotice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudyNotice/Comment -> 도메인 [StudyNoticeComment] 테스트")
class StudyNoticeCommentTest {
    private StudyNotice notice;

    @BeforeEach
    void setUp() {
        notice = StudyNotice.writeNotice(1L, 1L, "Hello", "Hello World");
    }


    @Test
    @DisplayName("스터디 공지사항에 작성한 댓글을 수정한다")
    void updateComment() {
        // given
        StudyNoticeComment comment = StudyNoticeComment.writeComment(notice, 1L, "Yes!!");

        // when
        comment.updateComment("No...");

        // then
        assertThat(comment.getContent()).isEqualTo("No...");
    }
}
