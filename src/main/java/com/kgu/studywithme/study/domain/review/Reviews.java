package com.kgu.studywithme.study.domain.review;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Reviews {
    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Review> reviews = new ArrayList<>();

    public static Reviews createReviewsPage() {
        return new Reviews();
    }

    public void writeReview(final Review review) {
        validateFirstReview(review.getWriter());
        reviews.add(review);
    }

    private void validateFirstReview(final Member writer) {
        if (isAlreadyWritten(writer)) {
            throw StudyWithMeException.type(StudyErrorCode.ALREADY_REVIEW_WRITTEN);
        }
    }

    private boolean isAlreadyWritten(final Member writer) {
        return reviews.stream()
                .anyMatch(review -> review.getWriter().isSameMember(writer));
    }
}
