package com.kgu.studywithme.favorite.application.usecase.command;

public record CancelStudyLikeCommand(
        Long memberId,
        Long studyId
) {
}
