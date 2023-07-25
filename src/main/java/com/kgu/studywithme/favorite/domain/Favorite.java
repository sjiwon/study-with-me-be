package com.kgu.studywithme.favorite.domain;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "favorite")
public class Favorite extends BaseEntity<Favorite> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private Favorite(
            final Long studyId,
            final Long memberId
    ) {
        this.studyId = studyId;
        this.memberId = memberId;
    }

    public static Favorite favoriteMarking(
            final Long studyId,
            final Long memberId
    ) {
        return new Favorite(studyId, memberId);
    }
}
