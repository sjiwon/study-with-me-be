package com.kgu.studywithme.studynotice.domain.model;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExecuteParallel
@DisplayName("StudyNotice/Comment -> 도메인 [StudyNoticeComment] 테스트")
class StudyNoticeCommentTest {
    private final Member writer = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = GHOST.toMember().apply(2L, LocalDateTime.now());
    private StudyNoticeComment comment;

    @BeforeEach
    void setUp() {
        final StudyNotice notice = StudyNotice
                .writeNotice(1L, writer.getId(), "Hello", "Hello World")
                .apply(1L, LocalDateTime.now());
        comment = StudyNoticeComment.writeComment(notice, writer.getId(), "Yes!!").apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("스터디 공지사항에 작성한 댓글을 수정한다")
    void updateComment() {
        // when
        comment.updateComment("No...");

        // then
        assertThat(comment.getContent()).isEqualTo("No...");
    }

    @Test
    @DisplayName("공지사항 댓글 작성자인지 확인한다")
    void isWriter() {
        // when
        final boolean actual1 = comment.isWriter(writer.getId());
        final boolean actual2 = comment.isWriter(anonymous.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}