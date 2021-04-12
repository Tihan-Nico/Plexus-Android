package com.plexus.services;

import android.content.Context;
import android.content.Intent;

import com.plexus.BuildConfig;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.jobs.UpdateLookoutJob;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.logging.Log;

import java.util.concurrent.TimeUnit;

public class UpdateApkRefreshListener extends PersistentAlarmManagerListener {

    private static final String TAG = UpdateApkRefreshListener.class.getSimpleName();

    private static final long INTERVAL = TimeUnit.HOURS.toMillis(6);

    public static void schedule(Context context) {
        new UpdateApkRefreshListener().onReceive(context, new Intent());
    }

    @Override
    protected long getNextScheduledExecutionTime(Context context) {
        return PlexusPreferences.getUpdateApkRefreshTime(context);
    }

    @Override
    protected long onAlarm(Context context, long scheduledTime) {
        Log.i(TAG, "onAlarm...");

        if (scheduledTime != 0 && BuildConfig.PLAY_STORE_DISABLED) {
            Log.i(TAG, "Queueing APK update job...");
            PlexusDependencies.getJobManager().add(new UpdateLookoutJob());
        }

        long newTime = System.currentTimeMillis() + INTERVAL;
        PlexusPreferences.setUpdateApkRefreshTime(context, newTime);

        return newTime;
    }

}
