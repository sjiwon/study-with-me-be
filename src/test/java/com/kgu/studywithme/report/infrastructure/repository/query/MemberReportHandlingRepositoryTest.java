package com.kgu.studywithme.report.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.report.domain.Report;
import com.kgu.studywithme.report.domain.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Report -> MemberReportHandlingRepository 테스트")
class MemberReportHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReportRepository reportRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(JIWON.toMember());
        memberB = memberRepository.save(GHOST.toMember());
    }

    @Test
    @DisplayName("특정 사용자에 대한 신고가 여전히 처리중인지 확인한다")
    void isReportStillPending() {
        // given
        Report report = reportRepository.save(
                Report.createReportWithReason(
                        memberA.getId(),
                        memberB.getId(),
                        "스터디를 대충합니다."
                )
        );

        // 1. 신고 접수
        assertThat(reportRepository.isReportStillPending(memberA.getId(), memberB.getId())).isTrue();

        // 2. 신고 승인
        report.approveReport();
        assertThat(reportRepository.isReportStillPending(memberA.getId(), memberB.getId())).isFalse();

        // 3. 신고 거부
        report.rejectReport();
        assertThat(reportRepository.isReportStillPending(memberA.getId(), memberB.getId())).isFalse();
    }
}
