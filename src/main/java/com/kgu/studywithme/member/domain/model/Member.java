package com.kgu.studywithme.member.domain.model;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @Embedded
    private Phone phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Address address;

    @Embedded
    private Score score;

    @Embedded
    private Interests interests;

    private Member(
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final Phone phone,
            final Gender gender,
            final Address address,
            final Set<Category> interests
    ) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.score = Score.init();
        this.interests = new Interests(this, interests);
    }

    public static Member createMember(
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final Phone phone,
            final Gender gender,
            final Address address,
            final Set<Category> interests
    ) {
        return new Member(name, nickname, email, birth, phone, gender, address, interests);
    }

    public void update(
            final Nickname nickname,
            final Phone phone,
            final Address address,
            final boolean emailOptIn,
            final Set<Category> interests
    ) {
        this.nickname = this.nickname.update(nickname.getValue());
        this.phone = phone;
        this.address = address;
        this.email = this.email.updateEmailOptIn(emailOptIn);
        this.interests.update(this, interests);
    }

    public void applyScoreByAttendanceStatus(final AttendanceStatus status) {
        switch (status) {
            case ATTENDANCE -> this.score = this.score.applyAttendance();
            case LATE -> this.score = this.score.applyLate();
            default -> this.score = this.score.applyAbsence();
        }
    }

    public void applyScoreByAttendanceStatus(final AttendanceStatus previous, final AttendanceStatus current) {
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

    public boolean isSameMember(final Member other) {
        return getId().equals(other.getId());
    }

    public boolean isEmailOptIn() {
        return email.isEmailOptIn();
    }

    public List<Category> getInterests() {
        return interests.getInterests()
                .stream()
                .map(Interest::getCategory)
                .toList();
    }
}
