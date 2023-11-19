package com.kgu.studywithme.studyparticipant.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyParticipant -> StudyParticipantRepository 테스트")
public class StudyParticipantRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyParticipantRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member applier;
    private Member participant;
    private Member leaveMember;
    private Member graduatedMember;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        applier = memberRepository.save(DUMMY1.toMember());
        participant = memberRepository.save(DUMMY2.toMember());
        leaveMember = memberRepository.save(DUMMY3.toMember());
        graduatedMember = memberRepository.save(DUMMY4.toMember());

        study = studyRepository.save(SPRING.toStudy(host));
        sut.saveAll(List.of(
                StudyParticipant.applyHost(study, host),
                StudyParticipant.applyInStudy(study, applier),
                StudyParticipant.applyParticipant(study, participant, APPROVE),
                StudyParticipant.applyParticipant(study, leaveMember, LEAVE),
                StudyParticipant.applyParticipant(study, graduatedMember, GRADUATED)
        ));
    }

    @Test
    @DisplayName("스터디 참여자를 ParticipantStatus에 따라 조회한다")
    void findParticipantByStatus() {
        assertAll(
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), APPLY)).isPresent(),
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), APPROVE)).isEmpty(),
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), GRADUATED)).isEmpty()
        );

        sut.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);
        assertAll(
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), APPLY)).isEmpty(),
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), GRADUATED)).isEmpty()
        );

        sut.updateParticipantStatus(study.getId(), applier.getId(), GRADUATED);
        assertAll(
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), APPLY)).isEmpty(),
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), APPROVE)).isEmpty(),
                () -> assertThat(sut.findParticipantByStatus(study.getId(), applier.getId(), GRADUATED)).isPresent()
        );
    }

    @Test
    @DisplayName("스터디 참여자들을 ParticipantStatus에 따라 조회한다")
    void findParticipantsByStatus() {
        final List<Member> applyParticipants1 = sut.findParticipantsByStatus(study.getId(), APPLY);
        final List<Member> approveParticipants1 = sut.findParticipantsByStatus(study.getId(), APPROVE);
        final List<Member> leaveParticipants1 = sut.findParticipantsByStatus(study.getId(), LEAVE);
        final List<Member> graduateParticipants1 = sut.findParticipantsByStatus(study.getId(), GRADUATED);
        assertAll(
                () -> assertThat(applyParticipants1).hasSize(1),
                () -> assertThat(applyParticipants1)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(applier.getId()),
                () -> assertThat(approveParticipants1).hasSize(2),
                () -> assertThat(approveParticipants1)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(host.getId(), participant.getId()),
                () -> assertThat(leaveParticipants1).hasSize(1),
                () -> assertThat(leaveParticipants1)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(leaveMember.getId()),
                () -> assertThat(graduateParticipants1).hasSize(1),
                () -> assertThat(graduateParticipants1)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(graduatedMember.getId())
        );

        /* applier -> participant */
        sut.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);

        final List<Member> applyParticipants2 = sut.findParticipantsByStatus(study.getId(), APPLY);
        final List<Member> approveParticipants2 = sut.findParticipantsByStatus(study.getId(), APPROVE);
        final List<Member> leaveParticipants2 = sut.findParticipantsByStatus(study.getId(), LEAVE);
        final List<Member> graduateParticipants2 = sut.findParticipantsByStatus(study.getId(), GRADUATED);
        assertAll(
                () -> assertThat(applyParticipants2).isEmpty(),
                () -> assertThat(approveParticipants2).hasSize(3),
                () -> assertThat(approveParticipants2)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(host.getId(), participant.getId(), applier.getId()),
                () -> assertThat(leaveParticipants2).hasSize(1),
                () -> assertThat(leaveParticipants2)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(leaveMember.getId()),
                () -> assertThat(graduateParticipants2).hasSize(1),
                () -> assertThat(graduateParticipants2)
                        .map(Member::getId)
                        .containsExactlyInAnyOrder(graduatedMember.getId())
        );
    }

    @Test
    @DisplayName("스터디 참여자인지 확인한다 (참여 상태 = APPROVE)")
    void isParticipant() {
        assertAll(
                () -> assertThat(sut.isParticipant(study.getId(), host.getId())).isTrue(),
                () -> assertThat(sut.isParticipant(study.getId(), applier.getId())).isFalse(),
                () -> assertThat(sut.isParticipant(study.getId(), participant.getId())).isTrue(),
                () -> assertThat(sut.isParticipant(study.getId(), leaveMember.getId())).isFalse(),
                () -> assertThat(sut.isParticipant(study.getId(), graduatedMember.getId())).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 졸업자인지 확인한다 (참여 상태 = GRADUATED)")
    void isGraduatedParticipant() {
        assertAll(
                () -> assertThat(sut.isGraduatedParticipant(study.getId(), host.getId())).isFalse(),
                () -> assertThat(sut.isGraduatedParticipant(study.getId(), applier.getId())).isFalse(),
                () -> assertThat(sut.isGraduatedParticipant(study.getId(), participant.getId())).isFalse(),
                () -> assertThat(sut.isGraduatedParticipant(study.getId(), leaveMember.getId())).isFalse(),
                () -> assertThat(sut.isGraduatedParticipant(study.getId(), graduatedMember.getId())).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 신청자 or 참여자인지 확인한다 (참여 상태 IN APPLY, APPROVE)")
    void isApplierOrParticipant() {
        assertAll(
                () -> assertThat(sut.isApplierOrParticipant(study.getId(), host.getId())).isTrue(),
                () -> assertThat(sut.isApplierOrParticipant(study.getId(), applier.getId())).isTrue(),
                () -> assertThat(sut.isApplierOrParticipant(study.getId(), participant.getId())).isTrue(),
                () -> assertThat(sut.isApplierOrParticipant(study.getId(), leaveMember.getId())).isFalse(),
                () -> assertThat(sut.isApplierOrParticipant(study.getId(), graduatedMember.getId())).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 참여 취소자 or 졸업자인지 확인한다 (참여 상태 IN LEAVE, GRADUATED)")
    void isAlreadyLeaveOrGraduatedParticipant() {
        assertAll(
                () -> assertThat(sut.isAlreadyLeaveOrGraduatedParticipant(study.getId(), host.getId())).isFalse(),
                () -> assertThat(sut.isAlreadyLeaveOrGraduatedParticipant(study.getId(), applier.getId())).isFalse(),
                () -> assertThat(sut.isAlreadyLeaveOrGraduatedParticipant(study.getId(), participant.getId())).isFalse(),
                () -> assertThat(sut.isAlreadyLeaveOrGraduatedParticipant(study.getId(), leaveMember.getId())).isTrue(),
                () -> assertThat(sut.isAlreadyLeaveOrGraduatedParticipant(study.getId(), graduatedMember.getId())).isTrue()
        );
    }
}
