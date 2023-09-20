package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyNotice -> StudyNoticeRepository 테스트")
public class StudyNoticeRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyNoticeRepository sut;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member writer;
    private Study study;

    @BeforeEach
    void setUp() {
        writer = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toStudy(writer.getId()));
    }

    @Test
    @DisplayName("공지사항을 수정한다 [제목, 내용]")
    void update() {
        // given
        final StudyNotice notice = sut.save(StudyNotice.writeNotice(study.getId(), writer.getId(), "Title", "Content"));

        // when
        sut.update(notice.getId(), "Title-Update", "Content-Update");

        // then
        final StudyNotice findNotice = sut.findById(notice.getId()).orElseThrow();
        assertAll(
                () -> assertThat(findNotice.getTitle()).isEqualTo("Title-Update"),
                () -> assertThat(findNotice.getContent()).isEqualTo("Content-Update")
        );
    }
}
