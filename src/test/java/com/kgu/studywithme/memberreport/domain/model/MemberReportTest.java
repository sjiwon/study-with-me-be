package com.kgu.studywithme.memberreport.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.APPROVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.RECEIVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberReport -> 도메인 [MemberReport] 테스트")
class MemberReportTest extends ParallelTest {
    private final Member memberA = JIWON.toMember().apply(1L);
    private final Member memberB = GHOST.toMember().apply(1L);
    private final String reason = "무단 결석을 10회나 했어요. 계정 정지시켜주세요.";

    @Test
    @DisplayName("특정 사용자에 대한 신고를 진행한다")
    void construct() {
        // when
        final MemberReport memberReport = MemberReport.createReport(memberA.getId(), memberB.getId(), reason);

        // then
        assertAll(
                () -> assertThat(memberReport.getReporterId()).isEqualTo(memberA.getId()),
                () -> assertThat(memberReport.getReporteeId()).isEqualTo(memberB.getId()),
                () -> assertThat(memberReport.getStatus()).isEqualTo(RECEIVE),
                () -> assertThat(memberReport.getReason()).isEqualTo(reason)
        );
    }

    @Test
    @DisplayName("관리자가 접수된 신고를 승인한다")
    void approveReport() {
        // given
        final MemberReport memberReport = MemberReport.createReport(memberA.getId(), memberB.getId(), reason);

        // when
        memberReport.approveReport();

        // then
        assertThat(memberReport.getStatus()).isEqualTo(APPROVE);
    }

    @Test
    @DisplayName("관리자가 접수된 신고를 거부한다")
    void rejectReport() {
        // given
        final MemberReport memberReport = MemberReport.createReport(memberA.getId(), memberB.getId(), reason);

        // when
        memberReport.rejectReport();

        // then
        assertThat(memberReport.getStatus()).isEqualTo(REJECT);
    }
}
