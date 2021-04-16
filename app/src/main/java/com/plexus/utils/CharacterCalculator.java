package com.plexus.utils;

import android.os.Parcel;

import androidx.annotation.NonNull;

public abstract class CharacterCalculator {

    public static CharacterCalculator readFromParcel(@NonNull Parcel in) {
        if (in.readInt() == 1) {
            return new PushCharacterCalculator();
        }
        throw new IllegalArgumentException("Read an unsupported value for a calculator.");
    }

    public static void writeToParcel(@NonNull Parcel dest, @NonNull CharacterCalculator calculator) {
        if (calculator instanceof PushCharacterCalculator) {
            dest.writeInt(3);
        } else {
            throw new IllegalArgumentException("Tried to write an unsupported calculator to a parcel.");
        }
    }

    public abstract CharacterState calculateCharacters(String messageBody);

    public static class CharacterState {
        public final int charactersRemaining;
        public final int messagesSpent;
        public final int maxTotalMessageSize;
        public final int maxPrimaryMessageSize;

        public CharacterState(int messagesSpent, int charactersRemaining, int maxTotalMessageSize, int maxPrimaryMessageSize) {
            this.messagesSpent = messagesSpent;
            this.charactersRemaining = charactersRemaining;
            this.maxTotalMessageSize = maxTotalMessageSize;
            this.maxPrimaryMessageSize = maxPrimaryMessageSize;
        }
    }
}
