package com.kgu.studywithme.memberreport.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreport.domain.repository.MemberReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("MemberReport -> MemberReportHandler 테스트")
public class MemberReportHandlerTest extends ParallelTest {
    private final MemberReportRepository memberReportRepository = mock(MemberReportRepository.class);
    private final MemberReportHandler sut = new MemberReportHandler(memberReportRepository);

    private final Member reporter = JIWON.toMember().apply(1L);
    private final Member reporteeA = GHOST.toMember().apply(2L);
    private final Member reporteeB = ANONYMOUS.toMember().apply(3L);

    @Test
    @DisplayName("특정 사용자에 대한 신고 내역이 처리중인지 확인한다")
    void isPreviousReportStillPending() {
        // given
        given(memberReportRepository.existsByReporterIdAndReporteeId(reporter.getId(), reporteeA.getId())).willReturn(true);
        given(memberReportRepository.existsByReporterIdAndReporteeId(reporter.getId(), reporteeB.getId())).willReturn(false);

        // when
        final boolean actual1 = sut.isPreviousReportStillPending(reporter.getId(), reporteeA.getId());
        final boolean actual2 = sut.isPreviousReportStillPending(reporter.getId(), reporteeB.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
