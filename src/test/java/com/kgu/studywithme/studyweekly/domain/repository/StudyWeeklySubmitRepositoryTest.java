package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly -> StudyWeeklySubmitRepository 테스트")
public class StudyWeeklySubmitRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyWeeklySubmitRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    private Member host;
    private final Member[] members = new Member[5];
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("해당 주차에 제출한 과제를 조회한다")
    void findSubmittedAssignment() {
        /* 1주차 X */
        final StudyWeekly weekly1 = studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId()));
        assertThat(sut.findSubmittedAssignment(host.getId(), weekly1.getId())).isEmpty();

        /* 1주차 O */
        weekly1.submitAssignment(host.getId(), UploadAssignment.withLink("https://notion.so/weekly1"));
        assertAll(
                () -> assertThat(sut.findSubmittedAssignment(weekly1.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly1.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly1")
        );

        /* 2주차 X */
        final StudyWeekly weekly2 = studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(study.getId(), host.getId()));
        assertAll(
                () -> assertThat(sut.findSubmittedAssignment(weekly1.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly1.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly1"),
                () -> assertThat(sut.findSubmittedAssignment(weekly2.getId(), host.getId())).isEmpty()
        );

        /* 2주차 O */
        weekly2.submitAssignment(host.getId(), UploadAssignment.withLink("https://notion.so/weekly2"));
        assertAll(
                () -> assertThat(sut.findSubmittedAssignment(weekly1.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly1.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly1"),
                () -> assertThat(sut.findSubmittedAssignment(weekly2.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly2.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly2")
        );

        /* 3주차 X */
        final StudyWeekly weekly3 = studyWeeklyRepository.save(STUDY_WEEKLY_3.toWeeklyWithAssignment(study.getId(), host.getId()));
        assertAll(
                () -> assertThat(sut.findSubmittedAssignment(weekly1.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly1.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly1"),
                () -> assertThat(sut.findSubmittedAssignment(weekly2.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly2.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly2"),
                () -> assertThat(sut.findSubmittedAssignment(weekly3.getId(), host.getId())).isEmpty()
        );

        /* 3주차 O */
        weekly3.submitAssignment(host.getId(), UploadAssignment.withFile("weekly3.pdf", "https://notion.so/weekly3"));
        assertAll(
                () -> assertThat(sut.findSubmittedAssignment(weekly1.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly1.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly1"),
                () -> assertThat(sut.findSubmittedAssignment(weekly2.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly2.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly2"),
                () -> assertThat(sut.findSubmittedAssignment(weekly3.getId(), host.getId())).isPresent(),
                () -> assertThat(sut.getSubmittedAssignment(weekly3.getId(), host.getId()).getUploadAssignment().getLink())
                        .isEqualTo("https://notion.so/weekly3")
        );
    }

    @Test
    @DisplayName("특정 Weekly에 제출한 과제 제출물을 삭제한다")
    void deleteFromSpecificWeekly() {
        /* 3 Weekly & Assignment Information */
        final StudyWeekly weekly1 = studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()));
        final StudyWeekly weekly2 = studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeekly(study.getId(), host.getId()));
        final StudyWeekly weekly3 = studyWeeklyRepository.save(STUDY_WEEKLY_3.toWeekly(study.getId(), host.getId()));

        sut.saveAll(List.of(
                // Weekly 1
                StudyWeeklySubmit.submitAssignment(weekly1, members[0].getId(), UploadAssignment.withLink("https://notion.so/weekly1/member0")),
                StudyWeeklySubmit.submitAssignment(weekly1, members[1].getId(), UploadAssignment.withLink("https://notion.so/weekly1/member1")),
                StudyWeeklySubmit.submitAssignment(weekly1, members[2].getId(), UploadAssignment.withLink("https://notion.so/weekly1/member2")),
                StudyWeeklySubmit.submitAssignment(weekly1, members[3].getId(), UploadAssignment.withLink("https://notion.so/weekly1/member3")),

                // Weekly 2
                StudyWeeklySubmit.submitAssignment(weekly2, members[0].getId(), UploadAssignment.withLink("https://notion.so/weekly2/member0")),
                StudyWeeklySubmit.submitAssignment(weekly2, members[1].getId(), UploadAssignment.withLink("https://notion.so/weekly2/member1")),
                StudyWeeklySubmit.submitAssignment(weekly2, members[2].getId(), UploadAssignment.withLink("https://notion.so/weekly2/member2")),
                StudyWeeklySubmit.submitAssignment(weekly2, members[3].getId(), UploadAssignment.withLink("https://notion.so/weekly2/member3")),
                StudyWeeklySubmit.submitAssignment(weekly2, members[4].getId(), UploadAssignment.withLink("https://notion.so/weekly2/member4")),

                // Weekly 3
                StudyWeeklySubmit.submitAssignment(weekly3, members[0].getId(), UploadAssignment.withLink("https://notion.so/weekly3/member0")),
                StudyWeeklySubmit.submitAssignment(weekly3, members[1].getId(), UploadAssignment.withLink("https://notion.so/weekly3/member1")),
                StudyWeeklySubmit.submitAssignment(weekly3, members[2].getId(), UploadAssignment.withLink("https://notion.so/weekly3/member2"))
        ));

        assertAll(
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly1.getId())).isEqualTo(4),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly2.getId())).isEqualTo(5),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly3.getId())).isEqualTo(3)
        );

        /* 3주차 과제 제출물 삭제 */
        final int deleteWeekly3 = sut.deleteFromSpecificWeekly(weekly3.getId());
        assertAll(
                () -> assertThat(deleteWeekly3).isEqualTo(3),
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly1.getId())).isEqualTo(4),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly2.getId())).isEqualTo(5),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isFalse()
        );

        /* 2주차 과제 제출물 삭제 */
        final int deleteWeekly2 = sut.deleteFromSpecificWeekly(weekly2.getId());
        assertAll(
                () -> assertThat(deleteWeekly2).isEqualTo(5),
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isTrue(),
                () -> assertThat(sut.countByWeeklyId(weekly1.getId())).isEqualTo(4),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isFalse(),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isFalse()
        );

        /* 1주차 과제 제출물 삭제 */
        final int deleteWeekly1 = sut.deleteFromSpecificWeekly(weekly1.getId());
        assertAll(
                () -> assertThat(deleteWeekly1).isEqualTo(4),
                () -> assertThat(sut.existsByWeeklyId(weekly1.getId())).isFalse(),
                () -> assertThat(sut.existsByWeeklyId(weekly2.getId())).isFalse(),
                () -> assertThat(sut.existsByWeeklyId(weekly3.getId())).isFalse()
        );
    }
}
