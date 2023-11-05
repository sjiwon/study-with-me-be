package com.kgu.studywithme.dummydata;

import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public record DummyMember(
        String name, String nickname, String email, Date birth, String phone,
        String gender, String province, String city, int score, int isEmailOptIn
) {
    public DummyMember(final int i) {
        this(
                "이름" + i,
                "닉네임" + i,
                createEmail(i),
                createBirth(),
                createPhone(i),
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
            postfix = "@gmail.com";
        }

        return "user" + i + postfix;
    }

    private static String createPhone(final int i) {
        final StringBuilder value = new StringBuilder(String.valueOf(i));
        while (value.length() != 8) {
            value.insert(0, "0");
        }

        final String value2 = value.toString();
        return "010-" + value2.substring(0, 4) + "-" + value2.substring(4);
    }

    private static Date createBirth() {
        final int year = ThreadLocalRandom.current().nextInt(1980, 2005 + 1);
        final int month = ThreadLocalRandom.current().nextInt(1, 13);
        final int day = ThreadLocalRandom.current().nextInt(1, LocalDate.of(year, month, 1).lengthOfMonth() + 1);
        final LocalDate localDate = LocalDate.of(year, month, day);
        return Date.valueOf(localDate);
    }
}
