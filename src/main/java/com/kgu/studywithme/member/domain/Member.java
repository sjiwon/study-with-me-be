package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.interest.Interest;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.domain.review.PeerReviews;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member extends BaseEntity<Member> {
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Email email;

    @Column(name = "birth", nullable = false, updatable = false)
    private LocalDate birth;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Region region;

    @Embedded
    private Score score;

    @Column(name = "is_email_opt_in", nullable = false)
    private boolean emailOptIn;

    @Embedded
    private PeerReviews peerReviews;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();

    private Member(
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final String phone,
            final Gender gender,
            final Region region,
            final boolean emailOptIn,
            final Set<Category> interests
    ) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.region = region;
        this.score = Score.initScore();
        this.emailOptIn = emailOptIn;
        this.peerReviews = PeerReviews.createPeerReviewsPage();
        applyInterests(interests);
    }

    public static Member createMember(
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final String phone,
            final Gender gender,
            final Region region,
            final boolean emailOptIn,
            final Set<Category> interests
    ) {
        return new Member(name, nickname, email, birth, phone, gender, region, emailOptIn, interests);
    }

    public void update(
            final String nickname,
            final String phone,
            final String province,
            final String city,
            final boolean emailOptIn,
            final Set<Category> interests
    ) {
        this.nickname = this.nickname.update(nickname);
        this.phone = phone;
        this.region = this.region.update(province, city);
        this.emailOptIn = emailOptIn;
        applyInterests(interests);
    }

    public void applyInterests(final Set<Category> interests) {
        this.interests.clear();
        this.interests.addAll(
                interests.stream()
                        .map(value -> Interest.applyInterest(this, value))
                        .toList()
        );
    }

    public boolean isSameMember(final Member member) {
        return this.email.isSameEmail(member.getEmail());
    }

    public void applyPeerReview(
            final Member reviewer,
            final String content
    ) {
        peerReviews.writeReview(PeerReview.doReview(this, reviewer, content));
    }

    public void applyScoreByAttendanceStatus(final AttendanceStatus status) {
        switch (status) {
            case ATTENDANCE -> this.score = this.score.applyAttendance();
            case LATE -> this.score = this.score.applyLate();
            default -> this.score = this.score.applyAbsence();
        }
    }

    public void applyScoreByAttendanceStatus(
            final AttendanceStatus previous,
            final AttendanceStatus current
    ) {
        switch (previous) {
            case ATTENDANCE -> updateAttenceToCurrent(current);
            case LATE -> updateLateToCurrent(current);
            default -> updateAbsenceToCurrent(current);
        }
    }

    private void updateAttenceToCurrent(final AttendanceStatus current) {
        switch (current) {
            case LATE -> this.score = this.score.updateAttendanceToLate();
            case ABSENCE -> this.score = this.score.updateAttendanceToAbsence();
        }
    }

    private void updateLateToCurrent(final AttendanceStatus current) {
        switch (current) {
            case ATTENDANCE -> this.score = this.score.updateLateToAttendance();
            case ABSENCE -> this.score = this.score.updateLateToAbsence();
        }
    }

    private void updateAbsenceToCurrent(final AttendanceStatus current) {
        switch (current) {
            case ATTENDANCE -> this.score = this.score.updateAbsenceToAttendance();
            case LATE -> this.score = this.score.updateAbsenceToLate();
        }
    }

    // Add Getter
    public String getNicknameValue() {
        return nickname.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public String getRegionProvince() {
        return region.getProvince();
    }

    public String getRegionCity() {
        return region.getCity();
    }

    public int getScore() {
        return score.getValue();
    }

    public List<PeerReview> getPeerReviews() {
        return peerReviews.getPeerReviews();
    }

    public List<Category> getInterests() {
        return interests.stream()
                .map(Interest::getCategory)
                .toList();
    }
}
