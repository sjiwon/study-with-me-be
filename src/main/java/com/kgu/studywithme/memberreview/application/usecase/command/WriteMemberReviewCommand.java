package com.kgu.studywithme.memberreview.application.usecase.command;

public record WriteMemberReviewCommand(
        Long reviewerId,
        Long revieweeId,
        String content
) {
}
