package com.securevault.tokenization.testdata;

import java.security.SecureRandom;
import java.util.HexFormat;

import org.apache.commons.lang3.RandomStringUtils;

public class PrimitiveDataProvider {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PrimitiveDataProvider() {
    }

    public static int getRandomInteger(int startInclusive, int endExclusive) {
        return RANDOM.nextInt(startInclusive, endExclusive);
    }

    public static String getRandomString() {
        return RandomStringUtils.secure().nextAlphabetic(10, 20);
    }

    public static String getRandomHexString() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public static String getRandomHexKey() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
