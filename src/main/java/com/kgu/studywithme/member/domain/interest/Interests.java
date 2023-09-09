package com.kgu.studywithme.member.domain.interest;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
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
public class Interests {
    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<Interest> interests = new ArrayList<>();

    public Interests(final Member member, final Set<Category> interests) {
        validateInterestsIsEmpty(interests);
        applyInterests(member, interests);
    }

    public void update(final Member member, final Set<Category> interests) {
        validateInterestsIsEmpty(interests);

        this.interests.clear();
        applyInterests(member, interests);
    }

    private void validateInterestsIsEmpty(final Set<Category> interests) {
        if (interests.isEmpty()) {
            throw StudyWithMeException.type(MemberErrorCode.INTEREST_MUST_EXISTS_AT_LEAST_ONE);
        }
    }

    private void applyInterests(final Member member, final Set<Category> interests) {
        this.interests.addAll(
                interests.stream()
                        .map(value -> Interest.applyInterest(member, value))
                        .toList()
        );
    }
}
