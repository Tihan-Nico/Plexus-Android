package com.plexus.utils;

import android.content.Context;
import android.hardware.Camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.plexus.utils.dynamic.DynamicTheme;

public class SecurePreferences {

    private static final String ATTACHMENT_ENCRYPTED_SECRET = "pref_attachment_encrypted_secret";
    private static final String ATTACHMENT_UNENCRYPTED_SECRET = "pref_attachment_unencrypted_secret";

    public static final String THEME_PREF = "pref_theme";
    public static final String LANGUAGE_PREF = "pref_language";

    private static final String GIF_GRID_LAYOUT = "pref_gif_grid_layout";

    private static final String LOCK_SCREEN_ENABLED = "pref_lock_screen_active";

    public static final String DIRECT_CAPTURE_CAMERA_ID = "pref_direct_capture_camera_id";

    public static void setDirectCaptureCameraId(Context context, int value) {
        setIntegerPrefrence(context, DIRECT_CAPTURE_CAMERA_ID, value);
    }

    @SuppressWarnings("deprecation")
    public static int getDirectCaptureCameraId(Context context) {
        return getIntegerPreference(context, DIRECT_CAPTURE_CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public static void setLockScreenActive(Context context, boolean value) {
        setBooleanPreference(context, LOCK_SCREEN_ENABLED, value);
    }

    public static boolean getLockScreenActive(Context context) {
        return getBooleanPreference(context, LOCK_SCREEN_ENABLED, false);
    }

    public static boolean isGifSearchInGridLayout(Context context) {
        return getBooleanPreference(context, GIF_GRID_LAYOUT, false);
    }

    public static void setIsGifSearchInGridLayout(Context context, boolean isGrid) {
        setBooleanPreference(context, GIF_GRID_LAYOUT, isGrid);
    }

    public static void setAttachmentEncryptedSecret(@NonNull Context context, @NonNull String secret) {
        setStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, secret);
    }

    public static void setAttachmentUnencryptedSecret(@NonNull Context context, @Nullable String secret) {
        setStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, secret);
    }

    public static @Nullable
    String getAttachmentEncryptedSecret(@NonNull Context context) {
        return getStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, null);
    }

    public static @Nullable
    String getAttachmentUnencryptedSecret(@NonNull Context context) {
        return getStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, null);
    }

    public static String getTheme(Context context) {
        return getStringPreference(context, THEME_PREF, DynamicTheme.systemThemeAvailable() ? DynamicTheme.SYSTEM : DynamicTheme.LIGHT);
    }

    public static String getLanguage(Context context) {
        return getStringPreference(context, LANGUAGE_PREF, "zz");
    }

    public static void setLanguage(Context context, String language) {
        setStringPreference(context, LANGUAGE_PREF, language);
    }

    public static void setStringPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    private static int getIntegerPreference(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    private static void setIntegerPrefrence(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    private static void removePreference(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }

}
