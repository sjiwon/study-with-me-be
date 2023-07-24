package com.kgu.studywithme.upload.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileUploadType {
    DESCRIPTION("description"),
    IMAGE("image"),
    ATTACHMENT("attachment"),
    SUBMIT("submit"),
    ;

    private final String value;
}
