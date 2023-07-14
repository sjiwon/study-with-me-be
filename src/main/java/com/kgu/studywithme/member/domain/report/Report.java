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
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "reportee_id", nullable = false)
    private Long reporteeId;

    @Lob
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    private Report(
            final Long reporterId,
            final Long reporteeId,
            final String reason
    ) {
        this.reporterId = reporterId;
        this.reporteeId = reporteeId;
        this.reason = reason;
        this.status = RECEIVE;
    }

    public static Report createReportWithReason(
            final Long reporterId,
            final Long reporteeId,
            final String reason
    ) {
        return new Report(reporterId, reporteeId, reason);
    }

    public void approveReport() {
        this.status = APPROVE;
    }

    public void rejectReport() {
        this.status = REJECT;
    }
}
