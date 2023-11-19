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

@DisplayName("StudyNotice -> 도메인 [StudyNotice] 테스트")
class StudyNoticeTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Member participant = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toStudy(host).apply(1L);

    @Test
    @DisplayName("스터디 공지사항 제목 & 내용을 수정한다")
    void updateNoticeInformation() {
        // given
        final StudyNotice notice = StudyNotice.writeNotice(study, host, "Hello", "Hello World").apply(1L);

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
        // given
        final StudyNotice notice = StudyNotice.writeNotice(study, host, "Hello", "Hello World").apply(1L);

        // when
        notice.addComment(host, "댓글 1");
        notice.addComment(participant, "댓글 2");
        notice.addComment(participant, "댓글 3");

        // then
        assertAll(
                () -> assertThat(notice.getComments()).hasSize(3),
                () -> assertThat(notice.getComments())
                        .map(StudyNoticeComment::getContent)
                        .containsExactlyInAnyOrder("댓글 1", "댓글 2", "댓글 3"),
                () -> assertThat(notice.getComments())
                        .map(StudyNoticeComment::getWriter)
                        .containsExactlyInAnyOrder(host, participant, participant)
        );
    }
}
