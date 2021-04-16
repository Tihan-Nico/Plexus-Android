package com.plexus.utils;

public class PushCharacterCalculator extends CharacterCalculator {
    private static final int MAX_TOTAL_SIZE = 64 * 1024;
    private static final int MAX_PRIMARY_SIZE = 2000;

    @Override
    public CharacterState calculateCharacters(String messageBody) {
        return new CharacterState(1, MAX_TOTAL_SIZE - messageBody.length(), MAX_TOTAL_SIZE, MAX_PRIMARY_SIZE);
    }
}
