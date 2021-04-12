package com.plexus.jobmanagers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Service that keeps the application in memory while the app is closed.
 * <p>
 * Important: Should only be used on API < 26.
 */
public class KeepAliveService extends Service {

    @Override
    public @Nullable
    IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
