package com.kgu.studywithme.member.domain.report;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.member.domain.report.ReportStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_report")
public class Report extends BaseEntity<Report> {
    @Column(name = "reportee_id", nullable = false)
    private Long reporteeId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Lob
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    private Report(
            final Long reporteeId,
            final Long reporterId,
            final String reason
    ) {
        this.reporteeId = reporteeId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.status = RECEIVE;
    }

    public static Report createReportWithReason(
            final Long reporteeId,
            final Long reporterId,
            final String reason
    ) {
        return new Report(reporteeId, reporterId, reason);
    }

    public void approveReport() {
        this.status = APPROVE;
    }

    public void rejectReport() {
        this.status = REJECT;
    }
}
