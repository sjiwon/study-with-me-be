package com.kgu.studywithme.study.domain.hashtag;

import com.kgu.studywithme.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_hashtag")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    private Hashtag(
            final Study study,
            final String name
    ) {
        this.study = study;
        this.name = name;
    }

    public static Hashtag applyHashtag(
            final Study study,
            final String name
    ) {
        return new Hashtag(study, name);
    }
}
