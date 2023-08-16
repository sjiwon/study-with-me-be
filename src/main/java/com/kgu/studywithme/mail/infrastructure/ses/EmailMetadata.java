package com.kgu.studywithme.mail.infrastructure.ses;

import java.time.format.DateTimeFormatter;

public interface EmailMetadata {
    // Template Name
    String APPROVE_TEMPLATE = "ParticipationApproveEmailTemplate";
    String REJECT_TEMPLATE = "ParticipationRejectEmailTemplate";
    String CERTIFICATE_TEMPLATE = "StudyCertificateEmailTemplate";

    // Email Title
    String PARTICIPATION_SUBJECT = "스터디 참여 신청 결과 안내드립니다.";
    String COMPLETION_SUBJECT = "스터디 수료증 관련 안내드립니다.";

    // 졸업 메일 -> 졸업 날짜 포맷
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
}
