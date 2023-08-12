package com.kgu.studywithme.common.utils;

import java.util.UUID;

public class OAuthUtils {
    public static final String GOOGLE_PROVIDER = "google";
    public static final String REDIRECT_URI = "https://localhost:3000/login";
    public static final String AUTHORIZATION_CODE = UUID.randomUUID().toString().replaceAll("-", "");
    public static final String STATE = UUID.randomUUID().toString().replaceAll("-", "");
}
