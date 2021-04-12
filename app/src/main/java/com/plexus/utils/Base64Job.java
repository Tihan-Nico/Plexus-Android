package com.plexus.utils;

import androidx.annotation.NonNull;

import java.io.IOException;

public final class Base64Job {

    private Base64Job() {
    }

    public static @NonNull
    byte[] decode(@NonNull String s) throws IOException {
        return Base64.decode(s);
    }

    public static @NonNull
    String encodeBytes(@NonNull byte[] source) {
        return Base64.encodeBytes(source);
    }

    public static @NonNull
    byte[] decodeOrThrow(@NonNull String s) {
        try {
            return Base64.decode(s);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }
}
