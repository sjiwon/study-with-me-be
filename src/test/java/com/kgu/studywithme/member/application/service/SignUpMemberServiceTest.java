package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SignUpMemberService 테스트")
class SignUpMemberServiceTest extends UseCaseTest {
    @InjectMocks
    private SignUpMemberService signUpMemberService;

    @Mock
    private MemberDuplicateCheckRepositoryAdapter memberDuplicateCheckRepositoryAdapter;

    @Mock
    private MemberRepository memberRepository;

    private final SignUpMemberUseCase.Command command = new SignUpMemberUseCase.Command(
            JIWON.getName(),
            JIWON.getNickname(),
            JIWON.getEmail(),
            JIWON.getBirth(),
            JIWON.getPhone(),
            JIWON.getGender(),
            JIWON.getAddress(),
            JIWON.getInterests()
    );

    @Test
    @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
    void throwExceptionByDuplicateEmail() {
        // given
        given(memberDuplicateCheckRepositoryAdapter.isEmailExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> signUpMemberService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isEmailExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(0)).isNicknameExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(0)).isPhoneExists(any()),
                () -> verify(memberRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberDuplicateCheckRepositoryAdapter.isEmailExists(any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isNicknameExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> signUpMemberService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isEmailExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isNicknameExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(0)).isPhoneExists(any()),
                () -> verify(memberRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberDuplicateCheckRepositoryAdapter.isEmailExists(any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isNicknameExists(any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isPhoneExists(any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> signUpMemberService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isEmailExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isNicknameExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isPhoneExists(any()),
                () -> verify(memberRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
    void success() {
        // given
        given(memberDuplicateCheckRepositoryAdapter.isEmailExists(any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isNicknameExists(any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isPhoneExists(any())).willReturn(false);

        final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
        given(memberRepository.save(any())).willReturn(member);

        // when
        final Long savedMemberId = signUpMemberService.invoke(command);

        // then
        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isEmailExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isNicknameExists(any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isPhoneExists(any()),
                () -> verify(memberRepository, times(1)).save(any()),
                () -> assertThat(savedMemberId).isEqualTo(member.getId())
        );
    }
}
