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
@DisplayName("StudyNotice -> 도메인 [StudyNotice] 테스트")
class StudyNoticeTest {
    private final Member writer = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = GHOST.toMember().apply(2L, LocalDateTime.now());
    private StudyNotice notice;

    @BeforeEach
    void setUp() {
        notice = StudyNotice.writeNotice(
                1L,
                writer.getId(),
                "Hello",
                "Hello World"
        ).apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("스터디 공지사항 제목 & 내용을 수정한다")
    void updateNoticeInformation() {
        // when
        notice.updateNoticeInformation("Notice 2", "Hello World222");

        // then
        assertAll(
                () -> assertThat(notice.getTitle()).isEqualTo("Notice 2"),
                () -> assertThat(notice.getContent()).isEqualTo("Hello World222")
        );
    }

    @Test
    @DisplayName("스터디 공지사항에 댓글을 작성한다")
    void addComment() {
        // when
        notice.addComment(1L, "댓글 1");
        notice.addComment(1L, "댓글 2");
        notice.addComment(2L, "댓글 3");
        notice.addComment(2L, "댓글 4");
        notice.addComment(2L, "댓글 5");

        // then
        assertAll(
                () -> assertThat(notice.getComments()).hasSize(5),
                () -> assertThat(notice.getComments())
                        .map(StudyNoticeComment::getContent)
                        .containsExactlyInAnyOrder("댓글 1", "댓글 2", "댓글 3", "댓글 4", "댓글 5"),
                () -> assertThat(notice.getComments())
                        .map(StudyNoticeComment::getWriterId)
                        .containsExactlyInAnyOrder(1L, 1L, 2L, 2L, 2L)
        );
    }
}
