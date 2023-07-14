package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.MemberRegistrationUseCase;
import com.kgu.studywithme.member.domain.*;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> MemberRegistrationService 테스트")
class MemberRegistrationServiceTest extends UseCaseTest {
    @InjectMocks
    private MemberRegistrationService memberRegistrationService;

    @Mock
    private MemberRepository memberRepository;

    private final MemberRegistrationUseCase.Command command =
            new MemberRegistrationUseCase.Command(
                    JIWON.getName(),
                    Nickname.from(JIWON.getNickname()),
                    Email.from(JIWON.getEmail()),
                    JIWON.getBirth(),
                    JIWON.getProvince(),
                    JIWON.getGender(),
                    Region.of(JIWON.getProvince(), JIWON.getCity()),
                    true,
                    JIWON.getInterests()
            );

    private final Member member = command.toEntity().apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
    void throwExceptionByDuplicateEmail() {
        // given
        given(memberRepository.existsByEmail(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberRegistrationService.registration(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

        verify(memberRepository, times(1)).existsByEmail(command.email());
        verify(memberRepository, times(0)).existsByNickname(command.nickname());
        verify(memberRepository, times(0)).existsByPhone(command.phone());
        verify(memberRepository, times(0)).save(member);
    }

    @Test
    @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByNickname(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberRegistrationService.registration(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        verify(memberRepository, times(1)).existsByEmail(any());
        verify(memberRepository, times(1)).existsByNickname(any());
        verify(memberRepository, times(0)).existsByPhone(any());
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByNickname(any())).willReturn(false);
        given(memberRepository.existsByPhone(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberRegistrationService.registration(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        verify(memberRepository, times(1)).existsByEmail(any());
        verify(memberRepository, times(1)).existsByNickname(any());
        verify(memberRepository, times(1)).existsByPhone(any());
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
    void success() {
        // given
        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByNickname(any())).willReturn(false);
        given(memberRepository.existsByPhone(any())).willReturn(false);
        given(memberRepository.save(any())).willReturn(member);

        // when
        Long savedMemberId = memberRegistrationService.registration(command);

        // then
        verify(memberRepository, times(1)).existsByEmail(any());
        verify(memberRepository, times(1)).existsByNickname(any());
        verify(memberRepository, times(1)).existsByPhone(any());
        verify(memberRepository, times(1)).save(any());
        assertThat(savedMemberId).isEqualTo(member.getId());
    }
}
