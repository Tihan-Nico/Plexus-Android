package com.plexus.utils;

import android.os.Handler;
import android.os.Looper;

public class Debouncer {
    private final Handler handler;
    private final long    threshold;

    /**
     * @param threshold Only one runnable will be executed via {@link #publish(Runnable)} every
     *                  {@code threshold} milliseconds.
     */
    public Debouncer(long threshold) {
        this.handler   = new Handler(Looper.getMainLooper());
        this.threshold = threshold;
    }

    public void publish(Runnable runnable) {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(runnable, threshold);
    }

    public void clear() {
        handler.removeCallbacksAndMessages(null);
    }
}
