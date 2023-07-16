//package com.kgu.studywithme.member.domain.review;
//
//import com.kgu.studywithme.global.exception.StudyWithMeException;
//import com.kgu.studywithme.member.domain.Member;
//import com.kgu.studywithme.member.exception.MemberErrorCode;
//import com.kgu.studywithme.peerreview.domain.PeerReview;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Embeddable;
//import jakarta.persistence.OneToMany;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Embeddable
//public class PeerReviews {
//    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.PERSIST)
//    private List<PeerReview> peerReviews = new ArrayList<>();
//
//    public static PeerReviews createPeerReviewsPage() {
//        return new PeerReviews();
//    }
//
//    public void writeReview(final PeerReview review) {
//        validateFirstReview(review.getReviewerId());
//        peerReviews.add(review);
//    }
//
//    private void validateFirstReview(final Member reviewer) {
//        if (isAlreadyReview(reviewer)) {
//            throw StudyWithMeException.type(MemberErrorCode.ALREADY_REVIEW);
//        }
//    }
//
//    private boolean isAlreadyReview(final Member reviewer) {
//        return peerReviews.stream()
//                .anyMatch(review -> review.getReviewerId().isSameMember(reviewer));
//    }
//}
