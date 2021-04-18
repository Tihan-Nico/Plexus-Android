package com.plexus.argon;

import java.util.Locale;

public final class ArgonException extends Exception {

    ArgonException(String message) {
        super(message);
    }

    ArgonException(int nativeErrorValue, String nativeErrorMessage) {
        this(String.format(Locale.US, "Argon failed %d: %s", nativeErrorValue, nativeErrorMessage));
    }
}
