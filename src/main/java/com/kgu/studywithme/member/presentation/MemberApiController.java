package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.aop.CheckMemberIdentity;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Gender;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Region;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import com.kgu.studywithme.member.presentation.dto.request.UpdateMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "3-1. 사용자 회원가입 & 정보 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {
    private final SignUpMemberUseCase signUpMemberUseCase;
    private final UpdateMemberUseCase updateMemberUseCase;

    @Operation(summary = "회원가입 EndPoint")
    @PostMapping("/member")
    public ResponseEntity<Void> signUp(@RequestBody @Valid final SignUpMemberRequest request) {
        final Long savedMemberId = signUpMemberUseCase.signUp(
                new SignUpMemberUseCase.Command(
                        request.name(),
                        new Nickname(request.nickname()),
                        new Email(request.email()),
                        request.birth(),
                        request.phone(),
                        Gender.from(request.gender()),
                        new Region(request.province(), request.city()),
                        request.emailOptIn(),
                        Category.of(request.interests())
                )
        );

        return ResponseEntity
                .created(
                        UriComponentsBuilder
                                .fromPath("/api/members/{id}")
                                .build(savedMemberId)
                )
                .build();
    }

    @Operation(summary = "사용자 정보 수정 EndPoint")
    @CheckMemberIdentity
    @PatchMapping("/members/{memberId}")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long payloadId,
            @PathVariable final Long memberId,
            @RequestBody @Valid final UpdateMemberRequest request
    ) {
        updateMemberUseCase.update(
                new UpdateMemberUseCase.Command(
                        memberId,
                        request.nickname(),
                        request.phone(),
                        request.province(),
                        request.city(),
                        request.emailOptIn(),
                        Category.of(request.interests())
                )
        );
        return ResponseEntity.noContent().build();
    }
}
