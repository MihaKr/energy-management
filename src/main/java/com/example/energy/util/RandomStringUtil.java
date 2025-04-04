package com.example.energy.util;

import org.apache.commons.text.RandomStringGenerator;

public class RandomStringUtil {
    private static final RandomStringGenerator GENERATOR = new RandomStringGenerator.Builder()
            .withinRange('a', 'z')
            .build();

    public static String generate(int length) {
        return GENERATOR.generate(length);
    }
}