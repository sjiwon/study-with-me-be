package com.kgu.studywithme.studyparticipant.domain.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(ParticipateMemberReaderImpl.class)
@DisplayName("StudyParticipant -> ParticipateMemberReader 테스트")
public class ParticipateMemberReaderTest extends RepositoryTest {
    @Autowired
    private ParticipateMemberReaderImpl sut;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

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

        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyHost(study.getId(), host.getId()),
                StudyParticipant.applyInStudy(study.getId(), applier.getId()),
                StudyParticipant.applyParticipant(study.getId(), participant.getId(), APPROVE),
                StudyParticipant.applyParticipant(study.getId(), leaveMember.getId(), LEAVE),
                StudyParticipant.applyParticipant(study.getId(), graduatedMember.getId(), GRADUATED)
        ));
    }

    @Test
    @DisplayName("특정 스터디 신청자를 조회한다 [With studyId + memberId]")
    void getApplier() {
        assertAll(
                () -> assertThatThrownBy(() -> sut.getApplier(study.getId(), host.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> sut.getApplier(study.getId(), participant.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> sut.getApplier(study.getId(), leaveMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> sut.getApplier(study.getId(), graduatedMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThat(sut.getApplier(study.getId(), applier.getId())).isEqualTo(applier)
        );
    }

    @Test
    @DisplayName("특정 스터디 참여자를 조회한다 [With studyId + memberId]")
    void getParticipant() {
        assertAll(
                () -> assertThatThrownBy(() -> sut.getParticipant(study.getId(), applier.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> sut.getParticipant(study.getId(), leaveMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> sut.getParticipant(study.getId(), graduatedMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage()),
                () -> assertThat(sut.getParticipant(study.getId(), host.getId())).isEqualTo(host),
                () -> assertThat(sut.getParticipant(study.getId(), participant.getId())).isEqualTo(participant)
        );
    }
}
