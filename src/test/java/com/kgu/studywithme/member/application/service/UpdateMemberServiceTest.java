package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> UpdateMemberService 테스트")
class UpdateMemberServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateMemberService memberUpdateService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberDuplicateCheckRepositoryAdapter memberDuplicateCheckRepositoryAdapter;

    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final UpdateMemberUseCase.Command command = new UpdateMemberUseCase.Command(
            member.getId(),
            GHOST.getNickname().getValue(),
            GHOST.getPhone().getValue(),
            GHOST.getAddress().getProvince(),
            GHOST.getAddress().getCity(),
            false,
            GHOST.getInterests()
    );

    @Test
    @DisplayName("다른 사람이 사용하고 있는 닉네임으로 수정할 수 없다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberRepository.getById(any())).willReturn(member);
        given(memberDuplicateCheckRepositoryAdapter.isNicknameUsedByOther(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberUpdateService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isNicknameUsedByOther(any(), any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(0)).isPhoneUsedByOther(any(), any())
        );
    }

    @Test
    @DisplayName("다른 사람이 사용하고 있는 전화번호로 수정할 수 없다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberRepository.getById(any())).willReturn(member);
        given(memberDuplicateCheckRepositoryAdapter.isNicknameUsedByOther(any(), any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isPhoneUsedByOther(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberUpdateService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isNicknameUsedByOther(any(), any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isPhoneUsedByOther(any(), any())
        );
    }

    @Test
    @DisplayName("사용자 정보를 수정한다")
    void success() {
        // given
        given(memberRepository.getById(any())).willReturn(member);
        given(memberDuplicateCheckRepositoryAdapter.isNicknameUsedByOther(any(), any())).willReturn(false);
        given(memberDuplicateCheckRepositoryAdapter.isPhoneUsedByOther(any(), any())).willReturn(false);

        // when
        memberUpdateService.invoke(command);

        // then
        assertAll(
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isNicknameUsedByOther(any(), any()),
                () -> verify(memberDuplicateCheckRepositoryAdapter, times(1)).isPhoneUsedByOther(any(), any()),
                () -> assertThat(member.getNickname().getValue()).isEqualTo(command.nickname()),
                () -> assertThat(member.getPhone().getValue()).isEqualTo(command.phone()),
                () -> assertThat(member.getAddress().getProvince()).isEqualTo(command.province()),
                () -> assertThat(member.getAddress().getCity()).isEqualTo(command.city()),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(command.emailOptIn()),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(command.interests())
        );
    }
}
