package com.kgu.studywithme.memberreport.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.memberreport.application.adapter.MemberReportHandlingRepositoryAdapter;
import com.kgu.studywithme.memberreport.domain.MemberReport;
import com.kgu.studywithme.memberreport.infrastructure.persistence.MemberReportJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;

@Import(MemberReportHandlingRepository.class)
@DisplayName("MemberReport -> MemberReportHandlingRepository 테스트")
class MemberReportHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberReportHandlingRepositoryAdapter memberReportHandlingRepositoryAdapter;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberReportJpaRepository memberReportJpaRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberJpaRepository.save(JIWON.toMember());
        memberB = memberJpaRepository.save(GHOST.toMember());
    }

    @Test
    @DisplayName("특정 사용자에 대한 신고가 여전히 처리중인지 확인한다")
    void isReportStillPending() {
        // given
        final MemberReport memberReport = memberReportJpaRepository.save(
                MemberReport.createReportWithReason(
                        memberA.getId(),
                        memberB.getId(),
                        "스터디를 대충합니다."
                )
        );

        // 1. 신고 접수
        assertThat(memberReportHandlingRepositoryAdapter.isReportStillPending(memberA.getId(), memberB.getId())).isTrue();

        // 2. 신고 승인
        memberReport.approveReport();
        assertThat(memberReportHandlingRepositoryAdapter.isReportStillPending(memberA.getId(), memberB.getId())).isFalse();

        // 3. 신고 거부
        memberReport.rejectReport();
        assertThat(memberReportHandlingRepositoryAdapter.isReportStillPending(memberA.getId(), memberB.getId())).isFalse();
    }
}