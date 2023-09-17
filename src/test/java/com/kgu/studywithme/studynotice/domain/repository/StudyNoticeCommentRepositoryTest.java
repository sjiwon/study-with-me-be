package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyNotice/Comment -> StudyNoticeCommentRepository 테스트")
public class StudyNoticeCommentRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyNoticeCommentRepository sut;

    @Autowired
    private StudyNoticeRepository studyNoticeRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("공지사항에 작성한 댓글을 삭제한다")
    void deleteByNoticeId() {
        // given
        final StudyNotice noticeA = StudyNotice.writeNotice(study.getId(), host.getId(), "Week 1", "Content Week 1");
        noticeA.addComment(host.getId(), "NoticeA Comment 1");
        noticeA.addComment(host.getId(), "NoticeA Comment 2");
        noticeA.addComment(host.getId(), "NoticeA Comment 3");
        studyNoticeRepository.save(noticeA);

        final StudyNotice noticeB = StudyNotice.writeNotice(study.getId(), host.getId(), "Week 2", "Content Week 2");
        noticeB.addComment(host.getId(), "NoticeB Comment 1");
        noticeB.addComment(host.getId(), "NoticeB Comment 2");
        noticeB.addComment(host.getId(), "NoticeB Comment 3");
        studyNoticeRepository.save(noticeB);

        final List<Long> commentIdsFromNoticeA = extractCommentIds(noticeA);
        final List<Long> commentIdsFromNoticeB = extractCommentIds(noticeB);
        assertAll(
                () -> assertThat(studyNoticeRepository.existsById(noticeA.getId())).isTrue(),
                () -> commentIdsFromNoticeA.forEach(commentIdFromNoticeA -> assertThat(sut.existsById(commentIdFromNoticeA)).isTrue()),
                () -> assertThat(studyNoticeRepository.existsById(noticeB.getId())).isTrue(),
                () -> commentIdsFromNoticeB.forEach(commentIdFromNoticeB -> assertThat(sut.existsById(commentIdFromNoticeB)).isTrue())
        );

        /* NoticeA 댓글 삭제 */
        sut.deleteByNoticeId(noticeA.getId());
        assertAll(
                () -> assertThat(studyNoticeRepository.existsById(noticeA.getId())).isTrue(),
                () -> commentIdsFromNoticeA.forEach(commentIdFromNoticeA -> assertThat(sut.existsById(commentIdFromNoticeA)).isFalse()),
                () -> assertThat(studyNoticeRepository.existsById(noticeB.getId())).isTrue(),
                () -> commentIdsFromNoticeB.forEach(commentIdFromNoticeB -> assertThat(sut.existsById(commentIdFromNoticeB)).isTrue())
        );

        /* NoticeB 댓글 삭제 */
        sut.deleteByNoticeId(noticeB.getId());
        assertAll(
                () -> assertThat(studyNoticeRepository.existsById(noticeA.getId())).isTrue(),
                () -> commentIdsFromNoticeA.forEach(commentIdFromNoticeA -> assertThat(sut.existsById(commentIdFromNoticeA)).isFalse()),
                () -> assertThat(studyNoticeRepository.existsById(noticeB.getId())).isTrue(),
                () -> commentIdsFromNoticeB.forEach(commentIdFromNoticeB -> assertThat(sut.existsById(commentIdFromNoticeB)).isFalse())
        );
    }

    private List<Long> extractCommentIds(final StudyNotice notice) {
        return notice.getComments()
                .stream()
                .map(BaseEntity::getId)
                .toList();
    }
}
