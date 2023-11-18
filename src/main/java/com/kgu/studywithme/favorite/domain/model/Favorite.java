package com.kgu.studywithme.favorite.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "favorite",
        indexes = {
                @Index(name = "idx_favorite_member_id_study_id", columnList = "member_id, study_id")
        }
)
public class Favorite extends BaseEntity<Favorite> {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    private Favorite(final Member member, final Study study) {
        this.member = member;
        this.study = study;
    }

    public static Favorite favoriteMarking(final Member member, final Study study) {
        return new Favorite(member, study);
    }
}
