package com.kgu.studywithme.memberreport.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberReportStatus {
    RECEIVE("신고 접수"),
    APPROVE("신고 승인"),
    REJECT("신고 거부"),
    ;

    private final String description;
}
