package com.kgu.studywithme.study.domain.participant;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static com.kgu.studywithme.study.domain.participant.ParticipantStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Participants {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    private Member host;

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Participant> participants = new ArrayList<>();

    @Embedded
    private Capacity capacity;

    private Participants(
            final Member host,
            final Capacity capacity
    ) {
        this.host = host;
        this.capacity = capacity;
    }

    public static Participants of(
            final Member host,
            final Capacity capacity
    ) {
        return new Participants(host, capacity);
    }

    public void delegateStudyHostAuthority(
            final Study study,
            final Member newHost
    ) {
        validateMemberIsParticipant(newHost);
        transferHostToParticipant(study, newHost);
    }

    private void transferHostToParticipant(
            final Study study,
            final Member newHost
    ) {
        // 1. 새로운 팀장을 참여자에서 제외
        participants.removeIf(participant -> participant.isSameMember(newHost));

        // 2. 현재 팀장을 참여자에 포함
        final Participant hostToParticipant = Participant.builder()
                .study(study)
                .member(host)
                .status(APPROVE)
                .build();
        participants.add(hostToParticipant);

        // 3. 팀장 교체
        host = newHost;
    }

    public void apply(
            final Study study,
            final Member member
    ) {
        validateMemberIsNotHost(member);
        validateMemberIsNotApplierOrParticipant(member);
        validateGraduateOrCancelRecordIsExists(member);
        participants.add(Participant.applyInStudy(study, member));
    }

    public void approve(final Member member) {
        validateMemberIsApplier(member);
        validateStudyCapacityIsNotYetFull();
        updateMemberParticipationStatus(member, APPROVE);
    }

    public void reject(final Member member) {
        validateMemberIsApplier(member);
        updateMemberParticipationStatus(member, REJECT);
    }

    public void cancel(final Member participant) {
        validateMemberIsNotHost(participant);
        validateMemberIsParticipant(participant);
        updateMemberParticipationStatus(participant, CALCEL);
    }

    public void graduate(final Member participant) {
        validateMemberIsNotHost(participant);
        validateMemberIsParticipant(participant);
        updateMemberParticipationStatus(participant, GRADUATED);
    }

    private void updateMemberParticipationStatus(
            final Member member,
            final ParticipantStatus status
    ) {
        participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .forEach(participant -> participant.updateStatus(status));
    }

    private void validateMemberIsNotHost(final Member member) {
        if (host.isSameMember(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_HOST);
        }
    }

    private void validateMemberIsNotApplierOrParticipant(final Member member) {
        if (isApplierOrParticipant(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_PARTICIPANT);
        }
    }

    private boolean isApplierOrParticipant(final Member member) {
        return participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .anyMatch(participant -> participant.getStatus() == APPLY || participant.getStatus() == APPROVE);
    }

    private void validateGraduateOrCancelRecordIsExists(final Member member) {
        if (isAlreadyGraduateOrCancel(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_ALREADY_GRADUATE_OR_CANCEL);
        }
    }

    private boolean isAlreadyGraduateOrCancel(final Member member) {
        return participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .anyMatch(participant -> participant.getStatus() == GRADUATED || participant.getStatus() == CALCEL);
    }

    public void validateMemberIsApplier(final Member member) {
        if (!isApplier(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_APPLIER);
        }
    }

    private boolean isApplier(final Member member) {
        return participants.stream()
                .filter(participant -> participant.isSameMember(member))
                .anyMatch(participant -> participant.getStatus() == APPLY);
    }

    private void validateStudyCapacityIsNotYetFull() {
        if (isFull()) {
            throw StudyWithMeException.type(StudyErrorCode.STUDY_CAPACITY_IS_FULL);
        }
    }

    public void updateCapacity(final int capacity) {
        if (getNumberOfApproveParticipants() > capacity) {
            throw StudyWithMeException.type(StudyErrorCode.CAPACITY_CANNOT_BE_LESS_THAN_PARTICIPANTS);
        }
        this.capacity = Capacity.from(capacity);
    }

    private boolean isFull() {
        return capacity.isEqualOrOver(getNumberOfApproveParticipants());
    }

    public void validateMemberIsParticipant(final Member participant) {
        if (!isParticipant(participant)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT);
        }
    }

    private boolean isParticipant(final Member member) {
        return getApproveParticipants().stream()
                .anyMatch(participant -> participant.isSameMember(member));
    }

    public void validateMemberIsStudyGraduate(final Member member) {
        if (!isGraduatedMember(member)) {
            throw StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_GRADUATED);
        }
    }

    private boolean isGraduatedMember(final Member member) {
        return getGraduatedParticipants().stream()
                .anyMatch(graduated -> graduated.isSameMember(member));
    }

    public List<Integer> getParticipantsAges() {
        return getApproveParticipants()
                .stream()
                .map(Member::getBirth)
                .map(birth -> Period.between(birth, LocalDate.now()).getYears())
                .toList();
    }

    public double getParticipantsAverageAge() {
        int sum = getParticipantsAges().stream()
                .mapToInt(age -> age)
                .sum();

        return (double) sum / getApproveParticipants().size();
    }

    public List<Member> getParticipants() {
        List<Member> members = participants.stream()
                .map(Participant::getMember)
                .toList();

        return Stream.of(List.of(host), members)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Member> getParticipantsWithoutHost() {
        return participants.stream()
                .map(Participant::getMember)
                .toList();
    }

    public List<Member> getApplier() {
        return participants.stream()
                .filter(participant -> participant.getStatus() == APPLY)
                .map(Participant::getMember)
                .toList();
    }

    public List<Member> getApproveParticipants() {
        List<Member> members = participants.stream()
                .filter(participant -> participant.getStatus() == APPROVE)
                .map(Participant::getMember)
                .toList();

        return Stream.of(List.of(host), members)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Member> getApproveParticipantsWithoutHost() {
        return participants.stream()
                .filter(participant -> participant.getStatus() == APPROVE)
                .map(Participant::getMember)
                .toList();
    }

    private int getNumberOfApproveParticipants() {
        return getApproveParticipants().size();
    }

    public List<Member> getGraduatedParticipants() {
        return participants.stream()
                .filter(participant -> participant.getStatus() == GRADUATED)
                .map(Participant::getMember)
                .toList();
    }
}
