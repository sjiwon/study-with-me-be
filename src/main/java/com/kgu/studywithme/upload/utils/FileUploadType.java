package com.kgu.studywithme.upload.utils;

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
