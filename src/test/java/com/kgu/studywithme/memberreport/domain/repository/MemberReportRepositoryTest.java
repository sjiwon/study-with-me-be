package com.kgu.studywithme.memberreport.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.memberreport.domain.model.MemberReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.APPROVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.RECEIVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberReport -> MemberReportRepository 테스트")
public class MemberReportRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberReportRepository sut;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(JIWON.toMember());
        memberB = memberRepository.save(GHOST.toMember());
    }

    @Test
    @DisplayName("특정 사용자에 대한 신고 내역을 처리 상태에 따라 존재하는지 확인한다")
    void existsByReporterIdAndReporteeId() {
        /* 신고 X */
        assertAll(
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), RECEIVE)).isFalse(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), APPROVE)).isFalse(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), REJECT)).isFalse()
        );

        /* 신고 O */
        final MemberReport report = sut.save(MemberReport.createReport(memberA.getId(), memberB.getId(), "스터디를 대충합니다."));
        assertAll(
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), RECEIVE)).isTrue(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), APPROVE)).isFalse(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), REJECT)).isFalse()
        );

        /* to 승인 */
        report.approveReport();
        assertAll(
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), RECEIVE)).isFalse(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), APPROVE)).isTrue(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), REJECT)).isFalse()
        );

        /* to 거부 */
        report.rejectReport();
        assertAll(
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), RECEIVE)).isFalse(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), APPROVE)).isFalse(),
                () -> assertThat(sut.existsByReporterIdAndReporteeIdAndStatus(memberA.getId(), memberB.getId(), REJECT)).isTrue()
        );
    }
}
