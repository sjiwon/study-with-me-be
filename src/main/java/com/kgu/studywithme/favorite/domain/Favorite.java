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
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    private Favorite(
            final Long memberId,
            final Long studyId
    ) {
        this.memberId = memberId;
        this.studyId = studyId;
    }

    public static Favorite favoriteMarking(
            final Long memberId,
            final Long studyId
    ) {
        return new Favorite(memberId, studyId);
    }
}
