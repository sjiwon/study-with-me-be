package com.kgu.studywithme.favorite.application.usecase.command;

public record MarkStudyLikeCommand(
        Long memberId,
        Long studyId
) {
}
