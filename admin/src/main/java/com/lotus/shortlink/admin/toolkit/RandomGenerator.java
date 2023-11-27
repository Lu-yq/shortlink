package com.lotus.shortlink.admin.toolkit;

import java.util.Random;

public final class RandomGenerator {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int LENGTH = 6;

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(LENGTH);
        Random random = new Random();

        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String randomString = generateRandomString();
        System.out.println(randomString);
    }
}
