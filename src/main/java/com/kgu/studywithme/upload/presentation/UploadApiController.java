package com.kgu.studywithme.upload.presentation;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import com.kgu.studywithme.upload.application.usecase.command.UploadStudyDescriptionImageUseCase;
import com.kgu.studywithme.upload.application.usecase.command.UploadWeeklyImageUseCase;
import com.kgu.studywithme.upload.presentation.dto.request.ImageUploadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "5. 파일 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UploadApiController {
    private final UploadWeeklyImageUseCase uploadWeeklyImageUseCase;
    private final UploadStudyDescriptionImageUseCase uploadStudyDescriptionImageUseCase;

    @Operation(summary = "이미지 업로드 EndPoint")
    @PostMapping(value = "/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SimpleResponseWrapper<String>> uploadImage(
            @ExtractPayload final Long memberId,
            @ModelAttribute @Valid final ImageUploadRequest request
    ) {
        final String imageUploadLink = uploadImageByType(request.type(), request.file());
        return ResponseEntity.ok(SimpleResponseWrapper.of(imageUploadLink));
    }

    private String uploadImageByType(String type, MultipartFile file) {
        if (type.equals("weekly")) {
            return uploadWeeklyImageUseCase.upload(new UploadWeeklyImageUseCase.Command(file));
        }
        return uploadStudyDescriptionImageUseCase.upload(new UploadStudyDescriptionImageUseCase.Command(file));
    }
}
