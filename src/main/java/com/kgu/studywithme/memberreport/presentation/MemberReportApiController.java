package com.kgu.studywithme.memberreport.presentation;

import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.resolver.Auth;
import com.kgu.studywithme.memberreport.application.usecase.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberCommand;
import com.kgu.studywithme.memberreport.presentation.dto.request.ReportMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3-4. 사용자 신고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{reporteeId}/report")
public class MemberReportApiController {
    private final ReportMemberUseCase reportMemberUseCase;

    @Operation(summary = "사용자 신고 EndPoint")
    @PostMapping
    public ResponseEntity<Void> report(
            @Auth final Authenticated authenticated,
            @PathVariable final Long reporteeId,
            @RequestBody @Valid final ReportMemberRequest request
    ) {
        reportMemberUseCase.invoke(new ReportMemberCommand(authenticated.id(), reporteeId, request.reason()));
        return ResponseEntity.noContent().build();
    }
}
