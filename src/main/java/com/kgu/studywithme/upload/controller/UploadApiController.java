package com.kgu.studywithme.upload.controller;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import com.kgu.studywithme.upload.controller.dto.request.ImageUploadRequest;
import com.kgu.studywithme.upload.utils.FileUploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "5. 파일 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UploadApiController {
    private final FileUploader uploader;

    @Operation(summary = "이미지 업로드 EndPoint")
    @PostMapping(value = "/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SimpleResponseWrapper<String>> uploadImage(@ExtractPayload Long memberId,
                                                                     @ModelAttribute @Valid ImageUploadRequest request) {
        String imageUploadLink = request.type().equals("weekly")
                ? uploader.uploadWeeklyImage(request.file())
                : uploader.uploadStudyDescriptionImage(request.file());

        return ResponseEntity.ok(new SimpleResponseWrapper<>(imageUploadLink));
    }
}
