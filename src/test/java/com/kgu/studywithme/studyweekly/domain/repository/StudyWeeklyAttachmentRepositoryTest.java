package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly/Attachment -> StudyWeeklyAttachmentRepository 테스트")
public class StudyWeeklyAttachmentRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyWeeklyAttachmentRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    private Member host;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toStudy(host));
    }

    @Test
    @DisplayName("특정 Weekly의 Attachment를 삭제한다")
    void deleteFromSpecificWeekly() {
        /* 3 Weekly */
        final StudyWeekly weekly1 = studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()));
        final StudyWeekly weekly2 = studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeekly(study.getId(), host.getId()));
        final StudyWeekly weekly3 = studyWeeklyRepository.save(STUDY_WEEKLY_3.toWeekly(study.getId(), host.getId()));

        assertAll(
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly1.getId())).isEqualTo(STUDY_WEEKLY_1.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly2.getId())).isEqualTo(STUDY_WEEKLY_2.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly3.getId())).isEqualTo(STUDY_WEEKLY_3.getAttachments().size())
        );

        /* 3주차 첨부파일 삭제 */
        final int deleteWeekly3 = sut.deleteFromSpecificWeekly(weekly3.getId());
        assertAll(
                () -> assertThat(deleteWeekly3).isEqualTo(STUDY_WEEKLY_3.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly1.getId())).isEqualTo(STUDY_WEEKLY_1.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly2.getId())).isEqualTo(STUDY_WEEKLY_2.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isFalse()
        );

        /* 2주차 첨부파일 삭제 */
        final int deleteWeekly2 = sut.deleteFromSpecificWeekly(weekly2.getId());
        assertAll(
                () -> assertThat(deleteWeekly2).isEqualTo(STUDY_WEEKLY_2.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly1.getId())).isEqualTo(STUDY_WEEKLY_1.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isFalse(),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isFalse()
        );

        /* 1주차 첨부파일 삭제 */
        final int deleteWeekly1 = sut.deleteFromSpecificWeekly(weekly1.getId());
        assertAll(
                () -> assertThat(deleteWeekly1).isEqualTo(STUDY_WEEKLY_1.getAttachments().size()),
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isFalse(),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isFalse(),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isFalse()
        );
    }
}
