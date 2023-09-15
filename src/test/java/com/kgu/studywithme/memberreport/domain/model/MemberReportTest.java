package com.kgu.studywithme.memberreport.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.APPROVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.RECEIVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberReport -> 도메인 [MemberReport] 테스트")
class MemberReportTest extends ParallelTest {
    private static final String REASON = "무단 결석을 10회나 했어요. 계정 정지시켜주세요.";

    @Test
    @DisplayName("특정 사용자에 대한 신고를 진행한다")
    void construct() {
        // when
        final MemberReport memberReport = MemberReport.createReportWithReason(1L, 2L, REASON);

        // then
        assertAll(
                () -> assertThat(memberReport.getStatus()).isEqualTo(RECEIVE),
                () -> assertThat(memberReport.getReason()).isEqualTo(REASON)
        );
    }

    @Test
    @DisplayName("관리자가 접수된 신고를 승인한다")
    void approveReport() {
        // given
        final MemberReport memberReport = MemberReport.createReportWithReason(1L, 2L, REASON);

        // when
        memberReport.approveReport();

        // then
        assertThat(memberReport.getStatus()).isEqualTo(APPROVE);
    }

    @Test
    @DisplayName("관리자가 접수된 신고를 거부한다")
    void rejectReport() {
        // given
        final MemberReport memberReport = MemberReport.createReportWithReason(1L, 2L, REASON);

        // when
        memberReport.rejectReport();

        // then
        assertThat(memberReport.getStatus()).isEqualTo(REJECT);
    }
}
