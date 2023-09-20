package com.kgu.studywithme.studynotice.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyNotice/Comment -> 도메인 [StudyNoticeComment] 테스트")
class StudyNoticeCommentTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Member anonymous = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final StudyNotice notice = StudyNotice.writeNotice(study.getId(), host.getId(), "Hello", "Hello World").apply(1L);

    @Test
    @DisplayName("스터디 공지사항에 작성한 댓글을 수정한다")
    void updateComment() {
        // given
        final StudyNoticeComment comment = StudyNoticeComment.writeComment(notice, host.getId(), "Yes!!").apply(1L);

        // when
        comment.updateComment("No...");

        // then
        assertThat(comment.getContent()).isEqualTo("No...");
    }

    @Test
    @DisplayName("공지사항 댓글 작성자인지 확인한다")
    void isWriter() {
        // given
        final StudyNoticeComment comment = StudyNoticeComment.writeComment(notice, host.getId(), "Yes!!").apply(1L);

        // when
        final boolean actual1 = comment.isWriter(host.getId());
        final boolean actual2 = comment.isWriter(anonymous.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
