package com.plexus.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

public class Util {

    private static volatile Handler handler;

    public static void wait(Object lock, long millis) {
        try {
            lock.wait(millis);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void runOnMainSync(final @NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            final CountDownLatch sync = new CountDownLatch(1);
            runOnMain(() -> {
                try {
                    runnable.run();
                } finally {
                    sync.countDown();
                }
            });
            try {
                sync.await();
            } catch (InterruptedException ie) {
                throw new AssertionError(ie);
            }
        }
    }

    public static void runOnMain(final @NonNull Runnable runnable) {
        if (isMainThread()) runnable.run();
        else getHandler().post(runnable);
    }

    private static Handler getHandler() {
        if (handler == null) {
            synchronized (Util.class) {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return handler;
    }

    public static int toIntExact(long value) {
        if ((int)value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)value;
    }

}
