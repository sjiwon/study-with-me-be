package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.global.dto.ResponseWrapper;
import com.kgu.studywithme.global.resolver.ExtractPayload;
import com.kgu.studywithme.member.application.usecase.MemberPrivateQueryUseCase;
import com.kgu.studywithme.member.application.usecase.query.GetAppliedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetLikeMarkedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetPrivateInformationById;
import com.kgu.studywithme.member.domain.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPrivateInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "3-2-2. 사용자 Private 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/me")
public class MemberPrivateInformationApiController {
    private final MemberPrivateQueryUseCase memberPrivateQueryUseCase;

    @Operation(summary = "사용자 기본 Private 정보 조회 EndPoint")
    @GetMapping
    public ResponseEntity<MemberPrivateInformation> getInformation(@ExtractPayload final Long memberId) {
        final MemberPrivateInformation response = memberPrivateQueryUseCase.getPrivateInformationById(new GetPrivateInformationById(memberId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자가 신청한 스터디 조회 EndPoint")
    @GetMapping("/studies/apply")
    public ResponseEntity<ResponseWrapper<List<AppliedStudy>>> getApplyStudy(@ExtractPayload final Long memberId) {
        final List<AppliedStudy> response = memberPrivateQueryUseCase.getAppliedStudyById(new GetAppliedStudyById(memberId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @Operation(summary = "사용자가 찜한 스터디 조회 EndPoint")
    @GetMapping("/studies/favorite")
    public ResponseEntity<ResponseWrapper<List<LikeMarkedStudy>>> getFavoriteStudy(@ExtractPayload final Long memberId) {
        final List<LikeMarkedStudy> response = memberPrivateQueryUseCase.getLikeMarkedStudyById(new GetLikeMarkedStudyById(memberId));
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }
}
