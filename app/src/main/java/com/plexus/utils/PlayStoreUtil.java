package com.plexus.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

public final class PlayStoreUtil {

    private PlayStoreUtil() {
    }

    public static void openPlayStore(@NonNull Context context) {
        String packageName = context.getPackageName();

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            CommunicationActions.openBrowserLink(context, "https://play.google.com/store/apps/details?id=" + packageName);
        }
    }

    public static void openPlayStoreForPlexus(@NonNull Context context) {
        String packageName = "com.plexus";

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            CommunicationActions.openBrowserLink(context, "https://play.google.com/store/apps/details?id=" + packageName);
        }
    }

}
