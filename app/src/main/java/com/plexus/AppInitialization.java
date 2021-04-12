package com.plexus;

import android.content.Context;

import androidx.annotation.NonNull;

import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.logging.Log;

/**
 * Rule of thumb: if there's something you want to do on the first app launch that involves
 * persisting state then add it here
 */
public final class AppInitialization {

    private static final String TAG = Log.tag(AppInitialization.class);

    private AppInitialization() {}

    public static void onFirstEverAppLaunch(@NonNull Context context) {
        Log.i(TAG, "onFirstEverAppLaunch()");

        PlexusPreferences.setLockScreenActive(context, false);
        /*PlexusPreferences.setLookoutNotificationEnabled(context, true);*/

    }

}
