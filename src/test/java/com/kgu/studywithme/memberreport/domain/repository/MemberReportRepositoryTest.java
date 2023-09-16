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
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MemberReport -> MemberReportRepository 테스트")
public class MemberReportRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberReportRepository memberReportRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(JIWON.toMember());
        memberB = memberRepository.save(GHOST.toMember());
    }

    @Test
    @DisplayName("특정 사용자에 대한 신고 내역이 존재하는지 확인한다")
    void existsByReporterIdAndReporteeId() {
        /* 신고 X */
        assertThat(memberReportRepository.existsByReporterIdAndReporteeId(memberA.getId(), memberB.getId())).isFalse();

        /* 신고 O */
        memberReportRepository.save(MemberReport.createReportWithReason(memberA.getId(), memberB.getId(), "스터디를 대충합니다."));
        assertThat(memberReportRepository.existsByReporterIdAndReporteeId(memberA.getId(), memberB.getId())).isTrue();
    }
}