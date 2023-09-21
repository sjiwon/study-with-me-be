package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.resolver.ExtractPayload;
import com.kgu.studywithme.member.application.usecase.SignUpMemberUseCase;
import com.kgu.studywithme.member.application.usecase.UpdateMemberUseCase;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberCommand;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberCommand;
import com.kgu.studywithme.member.domain.model.Address;
import com.kgu.studywithme.member.domain.model.Email;
import com.kgu.studywithme.member.domain.model.Gender;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import com.kgu.studywithme.member.presentation.dto.request.UpdateMemberRequest;
import com.kgu.studywithme.member.presentation.dto.response.MemberIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "3-1. 사용자 회원가입 & 정보 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {
    private final SignUpMemberUseCase signUpMemberUseCase;
    private final UpdateMemberUseCase updateMemberUseCase;

    @Operation(summary = "회원가입 EndPoint")
    @PostMapping
    public ResponseEntity<MemberIdResponse> signUp(@RequestBody @Valid final SignUpMemberRequest request) {
        final Long savedMemberId = signUpMemberUseCase.invoke(new SignUpMemberCommand(
                request.name(),
                new Nickname(request.nickname()),
                new Email(request.email(), request.emailOptIn()),
                request.birth(),
                new Phone(request.phone()),
                Gender.from(request.gender()),
                new Address(request.province(), request.city()),
                Category.of(request.interests())
        ));

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/members/{id}").build(savedMemberId))
                .body(new MemberIdResponse(savedMemberId));
    }

    @Operation(summary = "사용자 정보 수정 EndPoint")
    @CheckAuthUser
    @PatchMapping("/me")
    public ResponseEntity<Void> update(
            @ExtractPayload final Long memberId,
            @RequestBody @Valid final UpdateMemberRequest request
    ) {
        updateMemberUseCase.invoke(new UpdateMemberCommand(
                memberId,
                new Nickname(request.nickname()),
                new Phone(request.phone()),
                new Address(request.province(), request.city()),
                request.emailOptIn(),
                Category.of(request.interests())
        ));
        return ResponseEntity.noContent().build();
    }
}
