package com.kgu.studywithme.file.presentation;

import com.kgu.studywithme.file.application.usecase.UploadImageUseCase;
import com.kgu.studywithme.file.application.usecase.command.UploadImageCommand;
import com.kgu.studywithme.file.domain.model.FileUploadType;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.presentation.dto.request.ImageUploadRequest;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.aop.CheckAuthUser;
import com.kgu.studywithme.global.dto.ResponseWrapper;
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
public class FileUploadApiController {
    private final UploadImageUseCase uploadImageUseCase;

    @Operation(summary = "이미지 업로드 EndPoint")
    @CheckAuthUser
    @PostMapping(value = "/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> uploadImage(@ModelAttribute @Valid final ImageUploadRequest request) {
        final RawFileData rawFileData = FileConverter.convertImageFile(request.file(), FileUploadType.from(request.type()));
        final String imageUploadLink = uploadImageUseCase.invoke(new UploadImageCommand(rawFileData));
        return ResponseEntity.ok(ResponseWrapper.from(imageUploadLink));
    }
}
