package com.kgu.studywithme.member.infrastructure.repository.query;

public interface MemberDuplicateCheckQueryRepository {
    boolean isEmailExists(String email);

    boolean isNicknameExists(String nickname);

    boolean isNicknameUsedByOther(Long memberId, String nickname);

    boolean isPhoneExists(String phone);

    boolean isPhoneUsedByOther(Long memberId, String phone);
}
