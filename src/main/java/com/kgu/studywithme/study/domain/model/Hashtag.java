package com.kgu.studywithme.study.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
        name = "study_hashtag",
        indexes = {
                @Index(name = "idx_study_hashtag_study_id_name", columnList = "study_id, name")
        }
)
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    private Hashtag(final Study study, final String name) {
        this.study = study;
        this.name = name;
    }

    public static Hashtag applyHashtag(final Study study, final String name) {
        return new Hashtag(study, name);
    }
}
