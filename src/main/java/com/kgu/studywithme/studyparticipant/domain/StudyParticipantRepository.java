package com.kgu.studywithme.studyparticipant.domain;

import com.kgu.studywithme.studyparticipant.infrastructure.query.ParticipantHandlingRepository;
import com.kgu.studywithme.studyparticipant.infrastructure.query.ParticipantVerificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyParticipantRepository extends
        JpaRepository<StudyParticipant, Long>,
        ParticipantVerificationRepository,
        ParticipantHandlingRepository {
}
