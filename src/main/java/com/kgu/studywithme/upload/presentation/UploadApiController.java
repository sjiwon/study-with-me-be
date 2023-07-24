package com.kgu.studywithme.upload.presentation;

import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.dto.ResponseWrapper;
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
    @CheckAuthUser
    @PostMapping(value = "/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> uploadImage(
            @ModelAttribute @Valid final ImageUploadRequest request
    ) {
        final String imageUploadLink = uploadImageByType(request.type(), request.file());
        return ResponseEntity.ok(ResponseWrapper.from(imageUploadLink));
    }

    private String uploadImageByType(
            final String type,
            final MultipartFile file
    ) {
        if ("weekly".equals(type)) {
            return uploadWeeklyImageUseCase.uploadWeeklyImage(new UploadWeeklyImageUseCase.Command(file));
        }

        return uploadStudyDescriptionImageUseCase.uploadStudyDescriptionImage(
                new UploadStudyDescriptionImageUseCase.Command(file)
        );
    }
}
