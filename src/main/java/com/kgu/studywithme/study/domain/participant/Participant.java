package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.APPLY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_participant")
public class Participant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    private Participant(
            final Study study,
            final Member member,
            final ParticipantStatus status
    ) {
        this.study = study;
        this.member = member;
        this.status = status;
    }

    public static Participant applyInStudy(
            final Study study,
            final Member member
    ) {
        return new Participant(study, member, APPLY);
    }

    public boolean isSameMember(final Member other) {
        return this.member.isSameMember(other);
    }

    public void updateStatus(final ParticipantStatus status) {
        this.status = status;
    }
}
