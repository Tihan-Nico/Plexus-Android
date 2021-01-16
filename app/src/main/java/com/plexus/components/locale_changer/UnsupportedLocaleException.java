package com.plexus.components.locale_changer;

import android.os.Build;

import androidx.annotation.RequiresApi;

class UnsupportedLocaleException extends Exception {
    public UnsupportedLocaleException() {
    }

    public UnsupportedLocaleException(String message) {
        super(message);
    }

    public UnsupportedLocaleException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedLocaleException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UnsupportedLocaleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
