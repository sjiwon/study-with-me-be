package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.MemberFindService;
import com.kgu.studywithme.member.application.usecase.command.MemberUpdateUseCase;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> MemberUpdateService 테스트")
class MemberUpdateServiceTest extends UseCaseTest {
    @InjectMocks
    private MemberUpdateService memberUpdateService;

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private MemberRepository memberRepository;

    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());

    private final MemberUpdateUseCase.Command command =
            new MemberUpdateUseCase.Command(
                    member.getId(),
                    GHOST.getNickname(),
                    "01013572468",
                    GHOST.getProvince(),
                    GHOST.getCity(),
                    false,
                    GHOST.getInterests()
            );

    @Test
    @DisplayName("다른 사람이 사용하고 있는 닉네임으로 수정할 수 없다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberFindService.findById(any())).willReturn(member);
        given(memberRepository.isNicknameIsUsedByOther(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberUpdateService.update(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        verify(memberRepository, times(1)).isNicknameIsUsedByOther(any(), any());
        verify(memberRepository, times(0)).existsByIdNotAndPhone(any(), any());
    }

    @Test
    @DisplayName("다른 사람이 사용하고 있는 전화번호로 수정할 수 없다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberFindService.findById(any())).willReturn(member);
        given(memberRepository.isNicknameIsUsedByOther(any(), any())).willReturn(false);
        given(memberRepository.existsByIdNotAndPhone(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberUpdateService.update(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        verify(memberRepository, times(1)).isNicknameIsUsedByOther(any(), any());
        verify(memberRepository, times(1)).existsByIdNotAndPhone(any(), any());
    }

    @Test
    @DisplayName("사용자 정보를 수정한다")
    void success() {
        // given
        given(memberFindService.findById(any())).willReturn(member);
        given(memberRepository.isNicknameIsUsedByOther(any(), any())).willReturn(false);
        given(memberRepository.existsByIdNotAndPhone(any(), any())).willReturn(false);

        // when
        memberUpdateService.update(command);

        // then
        verify(memberRepository, times(1)).isNicknameIsUsedByOther(any(), any());
        verify(memberRepository, times(1)).existsByIdNotAndPhone(any(), any());

        assertAll(
                () -> assertThat(member.getNicknameValue()).isEqualTo(command.nickname()),
                () -> assertThat(member.getPhone()).isEqualTo(command.phone()),
                () -> assertThat(member.getRegionProvince()).isEqualTo(command.province()),
                () -> assertThat(member.getRegionCity()).isEqualTo(command.city()),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(command.emailOptIn()),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(command.interests())
        );
    }
}