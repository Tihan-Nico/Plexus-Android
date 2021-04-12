package com.plexus.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.plexus.core.utils.logging.Log;
import com.plexus.keyvalue.PlexusStore;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VersionTracker {

    private static final String TAG = Log.tag(VersionTracker.class);

    public static int getLastSeenVersion(@NonNull Context context) {
        return PlexusPreferences.getLastVersionCode(context);
    }

    public static void updateLastSeenVersion(@NonNull Context context) {
        try {
            int currentVersionCode = Util.getCanonicalVersionCode();
            int lastVersionCode = PlexusPreferences.getLastVersionCode(context);

            if (currentVersionCode != lastVersionCode) {
                Log.i(TAG, "Upgraded from " + lastVersionCode + " to " + currentVersionCode);
                PlexusStore.misc().clearClientDeprecated();
                PlexusPreferences.setLastVersionCode(context, currentVersionCode);
            }
        } catch (IOException ioe) {
            throw new AssertionError(ioe);
        }
    }

    public static long getDaysSinceFirstInstalled(Context context) {
        try {
            long installTimestamp = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;

            return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - installTimestamp);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
            return 0;
        }
    }

}
