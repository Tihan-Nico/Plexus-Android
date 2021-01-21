package com.plexus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class UpdateCheck {

    public static boolean appWasUpdated(Context context) throws PackageManager.NameNotFoundException {
        //this code gets current version-code (after upgrade it will show new versionCode)
        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
        int versionCode = info.versionCode;
        SharedPreferences prefs = context.getSharedPreferences("plexus", Context.MODE_PRIVATE);
        if (prefs.getInt("version", -1) > 0) {
            if (prefs.getInt("version", -1) != versionCode) {
                //save current versionCode: 1st-run after upgrade
                prefs.edit().putInt("version", versionCode).apply();

                return true;
            } //no need for else, because app version did not change...
        } else {
            //save current versionCode for 1st-run ever
            prefs.edit().putInt("version", versionCode).apply();
        }
        return false;
    }

}
