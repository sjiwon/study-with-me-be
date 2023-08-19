package com.kgu.studywithme.studynotice.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.infrastructure.persistence.StudyNoticeJpaRepository;
import com.kgu.studywithme.studynotice.infrastructure.persistence.comment.StudyNoticeCommentJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyNoticeHandlingRepository.class)
@DisplayName("StudyNotice -> StudyNoticeHandlingRepository 테스트")
class StudyNoticeHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyNoticeHandlingRepository studyNoticeHandlingRepository;

    @Autowired
    private StudyNoticeJpaRepository studyNoticeJpaRepository;

    @Autowired
    private StudyNoticeCommentJpaRepository studyNoticeCommentJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    @PersistenceContext
    private EntityManager em;

    private Member writer;
    private Study study;

    @BeforeEach
    void setUp() {
        writer = memberJpaRepository.save(JIWON.toMember());
        study = studyJpaRepository.save(SPRING.toOnlineStudy(writer.getId()));
    }

    @Test
    @DisplayName("공지사항을 수정한다 [제목, 내용]")
    void updateNotice() {
        // given
        final StudyNotice notice = studyNoticeJpaRepository.save(
                StudyNotice.writeNotice(
                        study.getId(),
                        writer.getId(),
                        "Title",
                        "Content"
                )
        );

        // when
        final long affectedRowCount = studyNoticeHandlingRepository.updateNotice(notice.getId(), "Title-Update", "Content-Update");
        syncAndClear(); // force

        // then
        final StudyNotice findNotice = studyNoticeJpaRepository.findById(notice.getId()).orElseThrow();
        assertAll(
                () -> assertThat(affectedRowCount).isEqualTo(1),
                () -> assertThat(findNotice.getTitle()).isEqualTo("Title-Update"),
                () -> assertThat(findNotice.getContent()).isEqualTo("Content-Update")
        );
    }

    @Test
    @DisplayName("공지사항을 삭제한다")
    void deleteNotice() {
        // given
        final StudyNotice notice = StudyNotice.writeNotice(
                study.getId(),
                writer.getId(),
                "Title",
                "Content"
        );
        notice.addComment(writer.getId(), "Comment 1");
        notice.addComment(writer.getId(), "Comment 2");
        notice.addComment(writer.getId(), "Comment 3");
        studyNoticeJpaRepository.save(notice);

        final List<Long> commendIds = extractCommentIds(notice);
        assertAll(
                () -> assertThat(studyNoticeJpaRepository.existsById(notice.getId())).isTrue(),
                () -> {
                    for (final Long commendId : commendIds) {
                        assertThat(studyNoticeCommentJpaRepository.existsById(commendId)).isTrue();
                    }
                }
        );

        // when
        studyNoticeHandlingRepository.deleteNotice(notice.getId());

        // then
        assertAll(
                () -> assertThat(studyNoticeJpaRepository.existsById(notice.getId())).isFalse(),
                () -> {
                    for (final Long commendId : commendIds) {
                        assertThat(studyNoticeCommentJpaRepository.existsById(commendId)).isFalse();
                    }
                }
        );
    }

    private List<Long> extractCommentIds(final StudyNotice notice) {
        return notice.getComments()
                .stream()
                .map(BaseEntity::getId)
                .toList();
    }

    private void syncAndClear() {
        em.flush();
        em.clear();
    }
}
