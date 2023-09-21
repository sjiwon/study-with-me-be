package com.kgu.studywithme.memberreview.application.usecase.command;

public record UpdateMemberReviewCommand(
        Long reviewerId,
        Long revieweeId,
        String content
) {
}
