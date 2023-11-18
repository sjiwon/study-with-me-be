package com.kgu.studywithme.memberreport.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.APPROVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.RECEIVE;
import static com.kgu.studywithme.memberreport.domain.model.MemberReportStatus.REJECT;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_report")
public class MemberReport extends BaseEntity<MemberReport> {
    @Lob
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false)
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reportee_id", referencedColumnName = "id", nullable = false)
    private Member reportee;

    private MemberReport(final Member reporter, final Member reportee, final String reason) {
        this.reporter = reporter;
        this.reportee = reportee;
        this.reason = reason;
        this.status = RECEIVE;
    }

    public static MemberReport createReport(final Member reporter, final Member reportee, final String reason) {
        return new MemberReport(reporter, reportee, reason);
    }

    public void approveReport() {
        this.status = APPROVE;
    }

    public void rejectReport() {
        this.status = REJECT;
    }
}
