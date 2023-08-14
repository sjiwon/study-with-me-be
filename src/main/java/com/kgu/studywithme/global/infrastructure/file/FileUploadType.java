package com.kgu.studywithme.global.infrastructure.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileUploadType {
    DESCRIPTION,
    IMAGE,
    ATTACHMENT,
    SUBMIT,
    ;
}
