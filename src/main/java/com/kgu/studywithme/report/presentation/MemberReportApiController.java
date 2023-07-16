package com.kgu.studywithme.report.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.report.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.report.presentation.dto.request.ReportMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3-4. 사용자 신고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{reporteeId}/report")
public class MemberReportApiController {
    private final ReportMemberUseCase reportMemberUseCase;

    @Operation(summary = "사용자 신고 EndPoint")
    @PostMapping
    public ResponseEntity<Void> report(
            @ExtractPayload final Long reporterId,
            @PathVariable final Long reporteeId,
            @RequestBody @Valid final ReportMemberRequest request
    ) {
        reportMemberUseCase.report(new ReportMemberUseCase.Command(reporterId, reporteeId, request.reason()));
        return ResponseEntity.noContent().build();
    }
}
