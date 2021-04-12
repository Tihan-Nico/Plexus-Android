package com.plexus.utils;

import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.core.utils.logging.Log;

import java.io.IOException;
import java.security.SecureRandom;

public class PlexusPreferences {

    private static final String TAG = PlexusPreferences.class.getSimpleName();
    public static final String PASSPHRASE_TIMEOUT_INTERVAL_PREF = "pref_timeout_interval";
    public static final String PASSPHRASE_TIMEOUT_PREF = "pref_timeout_passphrase";
    public static final String SCREEN_SECURITY_PREF = "pref_screen_security";
    public static final String DISABLE_PASSPHRASE_PREF = "pref_disable_passphrase";
    public static final String REGISTERED_GCM_PREF = "pref_gcm_registered";
    public static final String RINGTONE_PREF = "pref_key_ringtone";
    public static final String VIBRATE_PREF = "pref_key_vibrate";
    public static final String LED_COLOR_PREF = "pref_led_color";
    public static final String SCREEN_LOCK = "pref_android_screen_lock";
    public static final String SCREEN_LOCK_TIMEOUT = "pref_android_screen_lock_timeout";
    public static final String APP_THEME = "pref_app_dark_mode";
    public static final String APP_LANGUAGE = "pref_app_language";
    public static final String MAP_THEME_VIEW = "pref_map_theme_view";
    public static final String DISTRESS_SHORTCUT_EXIST = "pref_distress_shortcut_exists";
    public static final String DIRECT_CAPTURE_CAMERA_ID = "pref_direct_capture_camera_id";
    private static final String ATTACHMENT_ENCRYPTED_SECRET = "pref_attachment_encrypted_secret";
    private static final String ATTACHMENT_UNENCRYPTED_SECRET = "pref_attachment_unencrypted_secret";
    public  static final String BACKUP                      = "pref_backup";
    public  static final String BACKUP_ENABLED              = "pref_backup_enabled";
    private static final String BACKUP_PASSPHRASE           = "pref_backup_passphrase";
    private static final String ENCRYPTED_BACKUP_PASSPHRASE = "pref_encrypted_backup_passphrase";
    private static final String BACKUP_TIME                 = "pref_backup_next_time";
    private static final String NOTIFICATION_PREF = "pref_key_enable_notifications";
    private static final String NOTIFICATION_CHANNEL_VERSION = "pref_notification_channel_version";
    private static final String NOTIFICATION_MESSAGES_CHANNEL_VERSION = "pref_notification_messages_channel_version";
    private static final String NOTIFICATION_POSTS_CHANNEL_VERSION = "pref_notification_posts_channel_version";
    private static final String NOTIFICATION_KIDS_CHANNEL_VERSION = "pref_notification_kids_channel_version";
    private static final String LED_BLINK_PREF_CUSTOM = "pref_led_blink_custom";
    private static final String NEXT_PRE_KEY_ID = "pref_next_pre_key_id";
    private static final String ACTIVE_SIGNED_PRE_KEY_ID = "pref_active_signed_pre_key_id";
    private static final String NEXT_SIGNED_PRE_KEY_ID = "pref_next_signed_pre_key_id";
    private static final String GIF_GRID_LAYOUT = "pref_gif_grid_layout";
    private static final String HAS_SEEN_SWIPE_TO_REPLY = "pref_has_seen_swipe_to_reply";
    private static final String DATABASE_ENCRYPTED_SECRET = "pref_database_encrypted_secret";
    private static final String DATABASE_UNENCRYPTED_SECRET = "pref_database_unencrypted_secret";
    private static final String NEEDS_SQLCIPHER_MIGRATION = "pref_needs_sql_cipher_migration";
    private static final String SIGNED_PREKEY_REGISTERED_PREF = "pref_signed_prekey_registered";
    private static final String LOCK_SCREEN_ENABLED = "pref_lock_screen_active";
    private static final String WIFI_ONLY_ENABLED = "pref_wifi_only_active";
    private static final String SHAKE_DETECTION_ENABLED = "pref_shake_detection_active";
    private static final String LAST_VERSION_CODE_PREF = "last_version_code";
    private static final String UPDATE_APK_REFRESH_TIME_PREF = "pref_update_apk_refresh_time";
    private static final String UPDATE_APK_DOWNLOAD_ID = "pref_update_apk_download_id";
    private static final String UPDATE_APK_DIGEST = "pref_update_apk_digest";
    private static final String APP_LANGUAGE_NAME = "pref_app_language_name";
    private static final String MAP_SCALE_VIEW_ENABLED = "pref_map_scale_view";
    private static final String MAP_DISTANCE = "pref_map_distance";
    private static final String MAP_ANIMATIONS_ENABLED = "pref_map_animations";
    private static final String MAP_MARKER_LOCATION = "pref_map_marker_location";
    private static final String SERVICE_OUTAGE = "pref_service_outage";
    private static final String LAST_OUTAGE_CHECK_TIME = "pref_last_outage_check_time";
    private static final String APP_MIGRATION_VERSION = "pref_app_migration_version";
    private static final String FIRST_INSTALL_VERSION = "pref_first_install_version";
    private static final String SEEN_WELCOME_SCREEN_PREF = "pref_seen_welcome_screen";
    private static final String LAST_EXPERIENCE_VERSION_PREF = "last_experience_version_code";
    private static final String APP_DEPRECATED = "pref_app_deprecated";
    private static final String JOB_MANAGER_VERSION = "pref_job_manager_version";
    private static final String MEDIA_KEYBOARD_MODE = "pref_media_keyboard_mode";
    public static final String SYSTEM_EMOJI_PREF = "pref_system_emoji";
    private static final String LOG_ENCRYPTED_SECRET   = "pref_log_encrypted_secret";
    private static final String LOG_UNENCRYPTED_SECRET = "pref_log_unencrypted_secret";

    //APIs
    public static final String ANONYMOUS_TOKEN = "pref_anonymous_token";
    public static final String AUTH_TOKEN = "pref_auth_token";
    public static final String VERIFICATION_TOKEN = "pref_verification_token";
    public static final String TOKEN_TYPE = "pref_token_type";
    public static final String REFRESH_TOKEN = "pref_refresh_token";
    public static final String KEY_RETRIEVED_AT = "pref_key_retrieved";
    public static final String KEY_EXPIRES_IN = "pref_key_expires";
    public static final String AUTH_RETRIEVED_AT = "pref_auth_retrieved";
    public static final String AUTH_EXPIRES_IN = "pref_auth_expires";

    public static final String AUTH_TYPE = "pref_auth_type";
    public static final String AUTH_CREDENTIAL = "pref_auth_credential";
    public static final String AUTH_PASSWORD = "pref_auth_password";

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

    public static void setWifiOnlyModeActive(Context context, boolean value) {
        setBooleanPreference(context, WIFI_ONLY_ENABLED, value);
    }

    public static boolean getWifiOnlyModeActive(Context context) {
        return getBooleanPreference(context, WIFI_ONLY_ENABLED, false);
    }

    public static void setShakeDetectionActive(Context context, boolean value) {
        setBooleanPreference(context, SHAKE_DETECTION_ENABLED, value);
    }

    public static boolean getShakeDetectionActive(Context context) {
        return getBooleanPreference(context, SHAKE_DETECTION_ENABLED, true);
    }

    public static boolean hasSeenSwipeToReplyTooltip(Context context) {
        return getBooleanPreference(context, HAS_SEEN_SWIPE_TO_REPLY, false);
    }

    public static void setHasSeenSwipeToReplyTooltip(Context context, boolean value) {
        setBooleanPreference(context, HAS_SEEN_SWIPE_TO_REPLY, value);
    }

    public static void setAppThemeView(Context context, String value) {
        setStringPreference(context, APP_THEME, value);
    }

    public static String getAppThemeView(Context context) {
        return getStringPreference(context, APP_THEME, "Automatic");
    }

    public static void setAppLanguage(Context context, String value) {
        setStringPreference(context, APP_LANGUAGE, value);
    }

    public static String getAppLanguage(Context context) {
        return getStringPreference(context, APP_LANGUAGE, "EN");
    }

    public static void setAppLanguageName(Context context, String value) {
        setStringPreference(context, APP_LANGUAGE_NAME, value);
    }

    public static String getAppLanguageName(Context context) {
        return getStringPreference(context, APP_LANGUAGE_NAME, "English");
    }

    public static void setMapScaleViewActive(Context context, boolean value) {
        setBooleanPreference(context, MAP_SCALE_VIEW_ENABLED, value);
    }

    public static boolean getMapScaleViewActive(Context context) {
        return getBooleanPreference(context, MAP_SCALE_VIEW_ENABLED, true);
    }

    public static void setMapThemeView(Context context, String value) {
        setStringPreference(context, MAP_THEME_VIEW, value);
    }

    public static String getMapThemeView(Context context) {
        return getStringPreference(context, MAP_THEME_VIEW, "Automatic");
    }

    public static void setMapDistanceUnit(Context context, String value) {
        setStringPreference(context, MAP_DISTANCE, value);
    }

    public static String getMapDistanceUnit(Context context) {
        return getStringPreference(context, MAP_DISTANCE, "meters");
    }

    public static void setMapAnimationsActive(Context context, boolean value) {
        setBooleanPreference(context, MAP_ANIMATIONS_ENABLED, value);
    }

    public static boolean getMapAnimationsActive(Context context) {
        return getBooleanPreference(context, MAP_ANIMATIONS_ENABLED, true);
    }

    public static void setMapMarkerLocation(Context context, String value) {
        setStringPreference(context, MAP_MARKER_LOCATION, value);
    }

    public static String getMapMarkerLocation(Context context) {
        return getStringPreference(context, MAP_MARKER_LOCATION, "South-Africa");
    }

    public static boolean hasSeenWelcomeScreen(Context context) {
        return getBooleanPreference(context, SEEN_WELCOME_SCREEN_PREF, true);
    }

    public static void setDistressShortcutExist(Context context, boolean value) {
        setBooleanPreference(context, DISTRESS_SHORTCUT_EXIST, value);
    }

    public static boolean getDistressShortcutExist(Context context) {
        return getBooleanPreference(context, DISTRESS_SHORTCUT_EXIST, false);
    }

    public static void setHasSeenWelcomeScreen(Context context, boolean value) {
        setBooleanPreference(context, SEEN_WELCOME_SCREEN_PREF, value);
    }

    public static void setFirstInstallVersion(Context context, int version) {
        setIntegerPrefrence(context, FIRST_INSTALL_VERSION, version);
    }

    public static int getFirstInstallVersion(Context context) {
        return getIntegerPreference(context, FIRST_INSTALL_VERSION, -1);
    }

    public static int getLastExperienceVersionCode(Context context) {
        return getIntegerPreference(context, LAST_EXPERIENCE_VERSION_PREF, 0);
    }

    public static void setLastExperienceVersionCode(Context context, int versionCode) {
        setIntegerPrefrence(context, LAST_EXPERIENCE_VERSION_PREF, versionCode);
    }

    public static void setAppMigrationVersion(Context context, int version) {
        setIntegerPrefrence(context, APP_MIGRATION_VERSION, version);
    }

    public static int getAppMigrationVersion(Context context) {
        return getIntegerPreference(context, APP_MIGRATION_VERSION, 1);
    }

    public static void setClientDeprecated(Context context, boolean deprecated) {
        setBooleanPreference(context, APP_DEPRECATED, deprecated);
    }

    public static boolean isClientDeprecated(Context context) {
        return getBooleanPreference(context, APP_DEPRECATED, false);
    }

    public static void setJobManagerVersion(Context context, int version) {
        setIntegerPrefrence(context, JOB_MANAGER_VERSION, version);
    }

    public static int getJobManagerVersion(Context contex) {
        return getIntegerPreference(contex, JOB_MANAGER_VERSION, 1);
    }

    public static long getUpdateApkRefreshTime(Context context) {
        return getLongPreference(context, UPDATE_APK_REFRESH_TIME_PREF, 0L);
    }

    public static void setUpdateApkRefreshTime(Context context, long value) {
        setLongPreference(context, UPDATE_APK_REFRESH_TIME_PREF, value);
    }

    public static void setUpdateApkDownloadId(Context context, long value) {
        setLongPreference(context, UPDATE_APK_DOWNLOAD_ID, value);
    }

    public static long getUpdateApkDownloadId(Context context) {
        return getLongPreference(context, UPDATE_APK_DOWNLOAD_ID, -1);
    }

    public static void setUpdateApkDigest(Context context, String value) {
        setStringPreference(context, UPDATE_APK_DIGEST, value);
    }

    public static String getUpdateApkDigest(Context context) {
        return getStringPreference(context, UPDATE_APK_DIGEST, null);
    }

    public static void setBackupPassphrase(@NonNull Context context, @Nullable String passphrase) {
        setStringPreference(context, BACKUP_PASSPHRASE, passphrase);
    }

    public static @Nullable String getBackupPassphrase(@NonNull Context context) {
        return getStringPreference(context, BACKUP_PASSPHRASE, null);
    }

    public static void setEncryptedBackupPassphrase(@NonNull Context context, @Nullable String encryptedPassphrase) {
        setStringPreference(context, ENCRYPTED_BACKUP_PASSPHRASE, encryptedPassphrase);
    }

    public static @Nullable String getEncryptedBackupPassphrase(@NonNull Context context) {
        return getStringPreference(context, ENCRYPTED_BACKUP_PASSPHRASE, null);
    }

    public static void setBackupEnabled(@NonNull Context context, boolean value) {
        setBooleanPreference(context, BACKUP_ENABLED, value);
    }

    public static boolean isBackupEnabled(@NonNull Context context) {
        return getBooleanPreference(context, BACKUP_ENABLED, false);
    }

    public static void setNextBackupTime(@NonNull Context context, long time) {
        setLongPreference(context, BACKUP_TIME, time);
    }

    public static long getNextBackupTime(@NonNull Context context) {
        return getLongPreference(context, BACKUP_TIME, -1);
    }

    public static void setDatabaseEncryptedSecret(@NonNull Context context, @NonNull String secret) {
        setStringPreference(context, DATABASE_ENCRYPTED_SECRET, secret);
    }

    public static void setDatabaseUnencryptedSecret(@NonNull Context context, @Nullable String secret) {
        setStringPreference(context, DATABASE_UNENCRYPTED_SECRET, secret);
    }

    public static @Nullable
    String getDatabaseUnencryptedSecret(@NonNull Context context) {
        return getStringPreference(context, DATABASE_UNENCRYPTED_SECRET, null);
    }

    public static @Nullable
    String getDatabaseEncryptedSecret(@NonNull Context context) {
        return getStringPreference(context, DATABASE_ENCRYPTED_SECRET, null);
    }

    public static void setNeedsSqlCipherMigration(@NonNull Context context, boolean value) {
        setBooleanPreference(context, NEEDS_SQLCIPHER_MIGRATION, value);
    }

    public static boolean getNeedsSqlCipherMigration(@NonNull Context context) {
        return getBooleanPreference(context, NEEDS_SQLCIPHER_MIGRATION, false);
    }

    public static boolean isSignedPreKeyRegistered(Context context) {
        return getBooleanPreference(context, SIGNED_PREKEY_REGISTERED_PREF, false);
    }

    public static void setSignedPreKeyRegistered(Context context, boolean value) {
        setBooleanPreference(context, SIGNED_PREKEY_REGISTERED_PREF, value);
    }

    public static boolean isPushRegistered(Context context) {
        return getBooleanPreference(context, REGISTERED_GCM_PREF, false);
    }

    public static void setPushRegistered(Context context, boolean registered) {
        Log.i(TAG, "Setting push registered: " + registered);
        setBooleanPreference(context, REGISTERED_GCM_PREF, registered);
    }

    public static int getLastVersionCode(Context context) {
        return getIntegerPreference(context, LAST_VERSION_CODE_PREF, Util.getCanonicalVersionCode());
    }

    public static void setLastVersionCode(Context context, int versionCode) throws IOException {
        if (!setIntegerPreferenceBlocking(context, LAST_VERSION_CODE_PREF, versionCode)) {
            throw new IOException("couldn't write version code to sharedpreferences");
        }
    }

    public static boolean isScreenLockEnabled(@NonNull Context context) {
        return getBooleanPreference(context, SCREEN_LOCK, false);
    }

    public static void setScreenLockEnabled(@NonNull Context context, boolean value) {
        setBooleanPreference(context, SCREEN_LOCK, value);
    }

    public static int getNextPreKeyId(@NonNull Context context) {
        return getIntegerPreference(context, NEXT_PRE_KEY_ID, new SecureRandom().nextInt(Medium.MAX_VALUE));
    }

    public static void setNextPreKeyId(@NonNull Context context, int value) {
        setIntegerPrefrence(context, NEXT_PRE_KEY_ID, value);
    }

    public static int getNextSignedPreKeyId(@NonNull Context context) {
        return getIntegerPreference(context, NEXT_SIGNED_PRE_KEY_ID, new SecureRandom().nextInt(Medium.MAX_VALUE));
    }

    public static void setNextSignedPreKeyId(@NonNull Context context, int value) {
        setIntegerPrefrence(context, NEXT_SIGNED_PRE_KEY_ID, value);
    }

    public static int getActiveSignedPreKeyId(@NonNull Context context) {
        return getIntegerPreference(context, ACTIVE_SIGNED_PRE_KEY_ID, -1);
    }

    public static void setActiveSignedPreKeyId(@NonNull Context context, int value) {
        setIntegerPrefrence(context, ACTIVE_SIGNED_PRE_KEY_ID, value);
        ;
    }

    public static long getScreenLockTimeout(@NonNull Context context) {
        return getLongPreference(context, SCREEN_LOCK_TIMEOUT, 0);
    }

    public static void setScreenLockTimeout(@NonNull Context context, long value) {
        setLongPreference(context, SCREEN_LOCK_TIMEOUT, value);
    }

    public static void setLastOutageCheckTime(Context context, long timestamp) {
        setLongPreference(context, LAST_OUTAGE_CHECK_TIME, timestamp);
    }

    public static long getLastOutageCheckTime(Context context) {
        return getLongPreference(context, LAST_OUTAGE_CHECK_TIME, 0);
    }

    public static void setServiceOutage(Context context, boolean isOutage) {
        setBooleanPreference(context, SERVICE_OUTAGE, isOutage);
    }

    public static boolean getServiceOutage(Context context) {
        return getBooleanPreference(context, SERVICE_OUTAGE, false);
    }


    public static boolean isPasswordDisabled(Context context) {
        return getBooleanPreference(context, DISABLE_PASSPHRASE_PREF, false);
    }

    public static void setPasswordDisabled(Context context, boolean disabled) {
        setBooleanPreference(context, DISABLE_PASSPHRASE_PREF, disabled);
    }

    public static boolean isPassphraseTimeoutEnabled(Context context) {
        return getBooleanPreference(context, PASSPHRASE_TIMEOUT_PREF, false);
    }

    public static int getPassphraseTimeoutInterval(Context context) {
        return getIntegerPreference(context, PASSPHRASE_TIMEOUT_INTERVAL_PREF, 5 * 60);
    }

    public static void setPassphraseTimeoutInterval(Context context, int interval) {
        setIntegerPrefrence(context, PASSPHRASE_TIMEOUT_INTERVAL_PREF, interval);
    }

    public static void setScreenSecurityEnabled(Context context, boolean value) {
        setBooleanPreference(context, SCREEN_SECURITY_PREF, value);
    }

    public static boolean getScreenSecurityEnabled(Context context) {
        return getBooleanPreference(context, SCREEN_SECURITY_PREF, false);
    }

    public static int getNotificationChannelVersion(Context context) {
        return getIntegerPreference(context, NOTIFICATION_CHANNEL_VERSION, 1);
    }

    public static void setNotificationChannelVersion(Context context, int version) {
        setIntegerPrefrence(context, NOTIFICATION_CHANNEL_VERSION, version);
    }

    public static int getNotificationMessagesChannelVersion(Context context) {
        return getIntegerPreference(context, NOTIFICATION_MESSAGES_CHANNEL_VERSION, 1);
    }

    public static void setNotificationMessagesChannelVersion(Context context, int version) {
        setIntegerPrefrence(context, NOTIFICATION_MESSAGES_CHANNEL_VERSION, version);
    }

    public static int getNotificationPostsChannelVersion(Context context) {
        return getIntegerPreference(context, NOTIFICATION_POSTS_CHANNEL_VERSION, 1);
    }

    public static void setNotificationPostsChannelVersion(Context context, int version) {
        setIntegerPrefrence(context, NOTIFICATION_POSTS_CHANNEL_VERSION, version);
    }

    public static int getNotificationKidsChannelVersion(Context context) {
        return getIntegerPreference(context, NOTIFICATION_KIDS_CHANNEL_VERSION, 1);
    }

    public static void setNotificationKidsChannelVersion(Context context, int version) {
        setIntegerPrefrence(context, NOTIFICATION_KIDS_CHANNEL_VERSION, version);
    }

    public static void removeNotificationRingtone(Context context) {
        removePreference(context, RINGTONE_PREF);
    }

    public static void setNotificationRingtone(Context context, String ringtone) {
        setStringPreference(context, RINGTONE_PREF, ringtone);
    }

    public static void setNotificationVibrateEnabled(Context context, boolean enabled) {
        setBooleanPreference(context, VIBRATE_PREF, enabled);
    }

    public static boolean isNotificationVibrateEnabled(Context context) {
        return getBooleanPreference(context, VIBRATE_PREF, true);
    }


    public static String getNotificationLedColor(Context context) {
        return getStringPreference(context, LED_COLOR_PREF, "blue");
    }

    public static void setNotificationLedPatternCustom(Context context, String pattern) {
        setStringPreference(context, LED_BLINK_PREF_CUSTOM, pattern);
    }

    public static @NonNull
    Uri getNotificationRingtone(Context context) {
        String result = getStringPreference(context, RINGTONE_PREF, Settings.System.DEFAULT_NOTIFICATION_URI.toString());

        if (result != null && result.startsWith("file:")) {
            result = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
        }

        return Uri.parse(result);
    }

    public static boolean isNotificationsEnabled(Context context) {
        return getBooleanPreference(context, NOTIFICATION_PREF, true);
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

    public static void setMediaKeyboardMode(Context context, MediaKeyboardMode mode) {
        setStringPreference(context, MEDIA_KEYBOARD_MODE, mode.name());
    }

    public static MediaKeyboardMode getMediaKeyboardMode(Context context) {
        String name = getStringPreference(context, MEDIA_KEYBOARD_MODE, MediaKeyboardMode.EMOJI.name());
        return MediaKeyboardMode.valueOf(name);
    }

    public static boolean isSystemEmojiPreferred(Context context) {
        return getBooleanPreference(context, SYSTEM_EMOJI_PREF, false);
    }

    public static void setLogEncryptedSecret(Context context, String base64Secret) {
        setStringPreference(context, LOG_ENCRYPTED_SECRET, base64Secret);
    }

    public static String getLogEncryptedSecret(Context context) {
        return getStringPreference(context, LOG_ENCRYPTED_SECRET, null);
    }

    public static void setLogUnencryptedSecret(Context context, String base64Secret) {
        setStringPreference(context, LOG_UNENCRYPTED_SECRET, base64Secret);
    }

    public static String getLogUnencryptedSecret(Context context) {
        return getStringPreference(context, LOG_UNENCRYPTED_SECRET, null);
    }







    public static void setAnonymousToken(Context context, String value) {
        setStringPreference(context, ANONYMOUS_TOKEN, value);
    }

    public static String getAnonymousToken(Context context) {
        return getStringPreference(context, ANONYMOUS_TOKEN, "");
    }

    public static void setAuthToken(Context context, String value) {
        setStringPreference(context, AUTH_TOKEN, value);
    }

    public static String getAuthToken(Context context) {
        return getStringPreference(context, AUTH_TOKEN, null);
    }

    public static void setVerificationToken(Context context, String value) {
        setStringPreference(context, VERIFICATION_TOKEN, value);
    }

    public static String getVerificationToken(Context context) {
        return getStringPreference(context, VERIFICATION_TOKEN, "");
    }

    public static void setTokenType(Context context, String value) {
        setStringPreference(context, TOKEN_TYPE, value);
    }

    public static String getTokenType(Context context) {
        return getStringPreference(context, TOKEN_TYPE, "Bearer");
    }

    public static void setRefreshToken(Context context, String value) {
        setStringPreference(context, REFRESH_TOKEN, value);
    }

    public static String getRefreshToken(Context context) {
        return getStringPreference(context, REFRESH_TOKEN, null);
    }

    public static void setKeyExpiresIn(Context context, long value) {
        setLongPreference(context, KEY_EXPIRES_IN, value);
    }

    public static long getKeyExpiresIn(Context context) {
        return getLongPreference(context, KEY_EXPIRES_IN, 0);
    }

    public static void setKeyRetrievedAt(Context context, long value) {
        setLongPreference(context, KEY_RETRIEVED_AT, value);
    }

    public static long getKeyRetrievedAt(Context context) {
        return getLongPreference(context, KEY_RETRIEVED_AT, 0);
    }

    public static void setAuthExpiresIn(Context context, long value) {
        setLongPreference(context, AUTH_EXPIRES_IN, value);
    }

    public static long getAuthExpiresIn(Context context) {
        return getLongPreference(context, AUTH_EXPIRES_IN, 0);
    }

    public static void setAuthRetrievedAt(Context context, long value) {
        setLongPreference(context, AUTH_RETRIEVED_AT, value);
    }

    public static long getAuthRetrievedAt(Context context) {
        return getLongPreference(context, AUTH_RETRIEVED_AT, 0);
    }

    public static void setAuthType(Context context, String value) {
        setStringPreference(context, AUTH_TYPE, value);
    }

    public static String getAuthType(Context context) {
        return getStringPreference(context, AUTH_TYPE, "");
    }

    public static void setAuthCredential(Context context, String value) {
        setStringPreference(context, AUTH_CREDENTIAL, value);
    }

    public static String getAuthCredential(Context context) {
        return getStringPreference(context, AUTH_CREDENTIAL, "");
    }

    public static void setAuthPassword(Context context, String value) {
        setStringPreference(context, AUTH_PASSWORD, value);
    }

    public static String getAuthPassword(Context context) {
        return getStringPreference(context, AUTH_PASSWORD, "");
    }


    public static void setStringPreference(Context context, String key, String value) {
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    private static long getLongPreference(Context context, String key, long defaultValue) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }

    private static void setLongPreference(Context context, String key, long value) {
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
    }

    private static int getIntegerPreference(Context context, String key, int defaultValue) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    private static void setIntegerPrefrence(Context context, String key, int value) {
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    private static boolean setIntegerPreferenceBlocking(Context context, String key, int value) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
    }

    private static void removePreference(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }

    public static void removeAPIPreference(Context context) {
        removePreference(context, "pref_anonymous_token");
        removePreference(context, "pref_auth_token");
        removePreference(context, "pref_verification_token");
        removePreference(context, "pref_token_type");
        removePreference(context, "pref_refresh_token");
        removePreference(context, "pref_key_retrieved");
        removePreference(context, "pref_key_expires");
        removePreference(context, "pref_auth_retrieved");
        removePreference(context, "pref_auth_expires");
    }

    public enum MediaKeyboardMode {
        EMOJI
    }

}
