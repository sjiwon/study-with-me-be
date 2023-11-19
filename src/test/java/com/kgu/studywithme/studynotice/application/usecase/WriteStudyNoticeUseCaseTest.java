package com.kgu.studywithme.studynotice.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommand;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyNotice -> WriteStudyNoticeUseCase 테스트")
class WriteStudyNoticeUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final StudyNoticeRepository studyNoticeRepository = mock(StudyNoticeRepository.class);
    private final WriteStudyNoticeUseCase sut = new WriteStudyNoticeUseCase(
            studyRepository,
            memberRepository,
            studyNoticeRepository
    );

    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(host).apply(1L);
    private final WriteStudyNoticeCommand command = new WriteStudyNoticeCommand(host.getId(), study.getId(), "제목", "내용");

    @Test
    @DisplayName("스터디 공지사항을 작성한다")
    void success() {
        // given
        given(studyRepository.getById(command.studyId())).willReturn(study);
        given(memberRepository.getById(command.hostId())).willReturn(host);

        final StudyNotice notice = StudyNotice.writeNotice(study, host, command.title(), command.content()).apply(1L);
        given(studyNoticeRepository.save(any(StudyNotice.class))).willReturn(notice);

        // when
        final Long savedNoticeId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(command.studyId()),
                () -> verify(memberRepository, times(1)).getById(command.hostId()),
                () -> verify(studyNoticeRepository, times(1)).save(any(StudyNotice.class)),
                () -> assertThat(savedNoticeId).isEqualTo(notice.getId())
        );
    }
}
