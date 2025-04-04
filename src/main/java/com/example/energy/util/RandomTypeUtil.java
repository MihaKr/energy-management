package com.example.energy.util;

import java.util.Random;

public class RandomTypeUtil {
    private static final Random RANDOM = new Random();
    private static final String[] STRINGS = {
            "SOLAR",
            "HVAC",
            "METER"
    };

    public static String generate() {
        int randomIndex = RANDOM.nextInt(STRINGS.length);
        return STRINGS[randomIndex];
    }
}