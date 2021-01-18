package com.plexus.utils;

import android.os.Build;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public final class WindowUtil {

    private WindowUtil() {
    }

    public static void setStatusBarColor(@NonNull Window window, @ColorInt int color) {
        if (Build.VERSION.SDK_INT < 21) return;

        window.setStatusBarColor(color);
    }
}
