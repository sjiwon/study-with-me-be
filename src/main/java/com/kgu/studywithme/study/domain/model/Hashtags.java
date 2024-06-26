package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Hashtags {
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 5;

    @OneToMany(mappedBy = "study", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<Hashtag> hashtags = new ArrayList<>();

    public Hashtags(final Study study, final Set<String> hashtags) {
        validateHashtagCount(hashtags);
        applyHashtags(study, hashtags);
    }

    public void update(final Study study, final Set<String> hashtags) {
        validateHashtagCount(hashtags);

        this.hashtags.clear();
        applyHashtags(study, hashtags);
    }

    private void validateHashtagCount(final Set<String> hashtags) {
        if (hashtags.isEmpty()) {
            throw StudyWithMeException.type(StudyErrorCode.HASHTAG_MUST_EXISTS_AT_LEAST_ONE);
        }

        if (hashtags.size() > MAX_COUNT) {
            throw StudyWithMeException.type(StudyErrorCode.HASHTAG_MUST_NOT_EXISTS_MORE_THAN_FIVE);
        }
    }

    private void applyHashtags(final Study study, final Set<String> hashtags) {
        this.hashtags.addAll(
                hashtags.stream()
                        .map(value -> Hashtag.applyHashtag(study, value))
                        .toList()
        );
    }
}
