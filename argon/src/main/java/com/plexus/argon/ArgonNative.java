package com.plexus.argon;

final class ArgonNative {
    static final int OK = 0;

    static {
        System.loadLibrary("argon");
    }

    static native int hash(int tCost,
                           int mCost,
                           int parallelism,
                           byte[] pwd,
                           byte[] salt,
                           byte[] hash,
                           StringBuffer encoded,
                           int argon2Type,
                           int version);

    static native int verify(String encoded, byte[] pwd, int argon2Type);

    static native String resultToString(int argonResult);
}
