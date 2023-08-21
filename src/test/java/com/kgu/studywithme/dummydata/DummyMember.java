package com.kgu.studywithme.dummydata;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public record DummyMember(
        String name, String nickname, String email, Date birth, String phone,
        String gender, String province, String city, int score, int isEmailOptIn
) {
    private static final Set<String> PHONES = new HashSet<>();

    public DummyMember(final int i) {
        this(
                "이름" + i,
                "닉네임" + i,
                createEmail(i),
                createBirth(),
                createPhone(),
                (i % 5 != 0) ? "MALE" : "FEMALE",
                "경기도",
                "안양시",
                (int) (Math.random() * 51 + 50),
                1
        );
    }

    private static String createEmail(final int i) {
        final String postfix;
        if (i % 6 == 0) {
            postfix = "@kakao.com";
        } else if (i % 3 == 0) {
            postfix = "@naver.com";
        } else {
            postfix = "@google.com";
        }

        return "user" + i + postfix;
    }

    private static String createPhone() {
        String phoneNumber;
        do {
            phoneNumber = "010-" + (int) (Math.random() * 9000 + 1000) + "-" + (int) (Math.random() * 9000 + 1000);
        } while (!PHONES.add(phoneNumber));
        return phoneNumber;
    }

    private static Date createBirth() {
        final int year = ThreadLocalRandom.current().nextInt(1980, 2005 + 1);
        final int month = ThreadLocalRandom.current().nextInt(1, 13);
        final int day = ThreadLocalRandom.current().nextInt(1, LocalDate.of(year, month, 1).lengthOfMonth() + 1);
        final LocalDate localDate = LocalDate.of(year, month, day);
        return Date.valueOf(localDate);
    }
}
