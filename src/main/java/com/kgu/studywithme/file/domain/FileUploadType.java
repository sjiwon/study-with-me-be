package com.kgu.studywithme.file.domain;

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
