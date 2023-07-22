package com.kgu.studywithme.study.infrastructure.repository.query.dto;

import java.util.List;

public record StudyParticipant(
        StudyMember host,
        List<StudyMember> participants
) {
}
