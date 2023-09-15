package com.kgu.studywithme.studyparticipant.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "study_participant",
        indexes = {
                @Index(name = "idx_study_participant_member_id_status", columnList = "member_id, status"),
                @Index(name = "idx_study_participant_study_id_member_id_status", columnList = "study_id, member_id, status"),
                @Index(name = "idx_study_participant_study_id_status", columnList = "study_id, status")
        }
)
public class StudyParticipant extends BaseEntity<StudyParticipant> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @Builder
    private StudyParticipant(final Long studyId, final Long memberId, final ParticipantStatus status) {
        this.studyId = studyId;
        this.memberId = memberId;
        this.status = status;
    }

    public static StudyParticipant applyInStudy(final Long studyId, final Long memberId) {
        return new StudyParticipant(studyId, memberId, APPLY);
    }

    public static StudyParticipant applyHost(final Long studyId, final Long hostId) {
        return new StudyParticipant(studyId, hostId, APPROVE);
    }

    public static StudyParticipant applyParticipant(final Long studyId, final Long hostId, final ParticipantStatus status) {
        return new StudyParticipant(studyId, hostId, status);
    }
}
