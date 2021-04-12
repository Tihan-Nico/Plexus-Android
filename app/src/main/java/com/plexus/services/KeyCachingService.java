package com.plexus.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.plexus.BuildConfig;
import com.plexus.R;
import com.plexus.crypto.InvalidPassphraseException;
import com.plexus.crypto.MasterSecret;
import com.plexus.crypto.MasterSecretUtil;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.main.activity.MainActivity;
import com.plexus.migrations.ApplicationMigrations;
import com.plexus.notifications.NotificationChannels;
import com.plexus.utils.DynamicLanguage;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.ServiceUtil;
import com.plexus.utils.logging.Log;

import java.util.concurrent.TimeUnit;

/**
 * Small service that stays running to keep a key cached in memory.
 */

public class KeyCachingService extends Service {

    public static final int SERVICE_RUNNING_ID = 4141;
    public static final String KEY_PERMISSION = BuildConfig.APPLICATION_ID + ".ACCESS_SECRETS";
    public static final String NEW_KEY_EVENT = BuildConfig.APPLICATION_ID + ".service.action.NEW_KEY_EVENT";
    public static final String CLEAR_KEY_EVENT = BuildConfig.APPLICATION_ID + ".service.action.CLEAR_KEY_EVENT";
    public static final String LOCK_TOGGLED_EVENT = BuildConfig.APPLICATION_ID + ".service.action.LOCK_ENABLED_EVENT";
    public static final String CLEAR_KEY_ACTION = BuildConfig.APPLICATION_ID + ".service.action.CLEAR_KEY";
    public static final String DISABLE_ACTION = BuildConfig.APPLICATION_ID + ".service.action.DISABLE";
    public static final String LOCALE_CHANGE_EVENT = BuildConfig.APPLICATION_ID + ".service.action.LOCALE_CHANGE_EVENT";
    private static final String TAG = KeyCachingService.class.getSimpleName();
    private static final String PASSPHRASE_EXPIRED_EVENT = BuildConfig.APPLICATION_ID + ".service.action.PASSPHRASE_EXPIRED_EVENT";
    private static MasterSecret masterSecret;
    private final IBinder binder = new KeySetBinder();
    private DynamicLanguage dynamicLanguage = new DynamicLanguage();

    public KeyCachingService() {
    }

    public static synchronized boolean isLocked(Context context) {
        boolean locked = masterSecret == null && (!PlexusPreferences.isPasswordDisabled(context) || PlexusPreferences.isScreenLockEnabled(context));

        if (locked) {
            Log.d(TAG, "Locked! PasswordDisabled: " + PlexusPreferences.isPasswordDisabled(context) + ", ScreenLock: " + PlexusPreferences.isScreenLockEnabled(context));
        }

        return locked;
    }

    public static synchronized @Nullable
    MasterSecret getMasterSecret(Context context) {
        if (masterSecret == null && (PlexusPreferences.isPasswordDisabled(context) && !PlexusPreferences.isScreenLockEnabled(context))) {
            try {
                return MasterSecretUtil.getMasterSecret(context, MasterSecretUtil.UNENCRYPTED_PASSPHRASE);
            } catch (InvalidPassphraseException e) {
                Log.w(TAG, e);
            }
        }

        return masterSecret;
    }

    public static void onAppForegrounded(@NonNull Context context) {
        ServiceUtil.getAlarmManager(context).cancel(buildExpirationPendingIntent(context));
    }

    public static void onAppBackgrounded(@NonNull Context context) {
        startTimeoutIfAppropriate(context);
    }

    private static void startTimeoutIfAppropriate(@NonNull Context context) {
        boolean appVisible = PlexusDependencies.getAppForegroundObserver().isForegrounded();
        boolean secretSet = KeyCachingService.masterSecret != null;

        boolean timeoutEnabled = PlexusPreferences.isPassphraseTimeoutEnabled(context);
        boolean passLockActive = timeoutEnabled && !PlexusPreferences.isPasswordDisabled(context);

        long screenTimeout = PlexusPreferences.getScreenLockTimeout(context);
        boolean screenLockActive = screenTimeout >= 60 && PlexusPreferences.isScreenLockEnabled(context);

        if (!appVisible && secretSet && (passLockActive || screenLockActive)) {
            long passphraseTimeoutMinutes = PlexusPreferences.getPassphraseTimeoutInterval(context);
            long screenLockTimeoutSeconds = PlexusPreferences.getScreenLockTimeout(context);

            long timeoutMillis;

            if (!PlexusPreferences.isPasswordDisabled(context))
                timeoutMillis = TimeUnit.MINUTES.toMillis(passphraseTimeoutMinutes);
            else timeoutMillis = TimeUnit.SECONDS.toMillis(screenLockTimeoutSeconds);

            Log.i(TAG, "Starting timeout: " + timeoutMillis);

            AlarmManager alarmManager = ServiceUtil.getAlarmManager(context);
            PendingIntent expirationIntent = buildExpirationPendingIntent(context);

            alarmManager.cancel(expirationIntent);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + timeoutMillis, expirationIntent);
        }
    }

    private static PendingIntent buildExpirationPendingIntent(@NonNull Context context) {
        Intent expirationIntent = new Intent(PASSPHRASE_EXPIRED_EVENT, null, context, KeyCachingService.class);
        return PendingIntent.getService(context, 0, expirationIntent, 0);
    }

    @SuppressLint("StaticFieldLeak")
    public void setMasterSecret(final MasterSecret masterSecret) {
        synchronized (KeyCachingService.class) {
            KeyCachingService.masterSecret = masterSecret;

            foregroundService();
            broadcastNewSecret();
            startTimeoutIfAppropriate(this);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    if (!ApplicationMigrations.isUpdate(KeyCachingService.this)) {
                        PlexusDependencies.getMessageNotifier().updateNotification(KeyCachingService.this);
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        Log.d(TAG, "onStartCommand, " + intent.getAction());

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case CLEAR_KEY_ACTION:
                    handleClearKey();
                    break;
                case PASSPHRASE_EXPIRED_EVENT:
                    handleClearKey();
                    break;
                case DISABLE_ACTION:
                    handleDisableService();
                    break;
                case LOCALE_CHANGE_EVENT:
                    handleLocaleChanged();
                    break;
                case LOCK_TOGGLED_EVENT:
                    handleLockToggled();
                    break;
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();

        if (PlexusPreferences.isPasswordDisabled(this) && !PlexusPreferences.isScreenLockEnabled(this)) {
            try {
                MasterSecret masterSecret = MasterSecretUtil.getMasterSecret(this, MasterSecretUtil.UNENCRYPTED_PASSPHRASE);
                setMasterSecret(masterSecret);
            } catch (InvalidPassphraseException e) {
                Log.w(TAG, e);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "KCS Is Being Destroyed!");
        handleClearKey();
    }

    @SuppressLint("StaticFieldLeak")
    private void handleClearKey() {
        Log.i(TAG, "handleClearKey()");
        KeyCachingService.masterSecret = null;
        stopForeground(true);

        Intent intent = new Intent(CLEAR_KEY_EVENT);
        intent.setPackage(getApplicationContext().getPackageName());

        sendBroadcast(intent, KEY_PERMISSION);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                PlexusDependencies.getMessageNotifier().updateNotification(KeyCachingService.this);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleLockToggled() {
        stopForeground(true);

        try {
            MasterSecret masterSecret = MasterSecretUtil.getMasterSecret(this, MasterSecretUtil.UNENCRYPTED_PASSPHRASE);
            setMasterSecret(masterSecret);
        } catch (InvalidPassphraseException e) {
            Log.w(TAG, e);
        }
    }

    private void handleDisableService() {
        if (PlexusPreferences.isPasswordDisabled(this) &&
                !PlexusPreferences.isScreenLockEnabled(this)) {
            stopForeground(true);
        }
    }

    private void handleLocaleChanged() {
        dynamicLanguage.updateServiceLocale(this);
        foregroundService();
    }

    private void foregroundService() {
        if (PlexusPreferences.isPasswordDisabled(this) && !PlexusPreferences.isScreenLockEnabled(this)) {
            stopForeground(true);
            return;
        }

        Log.i(TAG, "foregrounding KCS");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannels.LOCKED_STATUS);

        builder.setContentTitle(getString(R.string.KeyCachingService_passphrase_cached));
        builder.setContentText(getString(R.string.KeyCachingService_lookout_passphrase_cached));
        //Should be an open lock
        builder.setSmallIcon(R.drawable.ic_lock);
        builder.setWhen(0);
        builder.setPriority(Notification.PRIORITY_MIN);

        builder.addAction(R.drawable.ic_lock, getString(R.string.KeyCachingService_lock), buildLockIntent());
        builder.setContentIntent(buildLaunchIntent());

        stopForeground(true);
        startForeground(SERVICE_RUNNING_ID, builder.build());
    }

    private void broadcastNewSecret() {
        Log.i(TAG, "Broadcasting new secret...");

        Intent intent = new Intent(NEW_KEY_EVENT);
        intent.setPackage(getApplicationContext().getPackageName());

        sendBroadcast(intent, KEY_PERMISSION);
    }

    private PendingIntent buildLockIntent() {
        Intent intent = new Intent(this, KeyCachingService.class);
        intent.setAction(PASSPHRASE_EXPIRED_EVENT);
        return PendingIntent.getService(getApplicationContext(), 0, intent, 0);
    }

    private PendingIntent buildLaunchIntent() {
        // TODO [greyson] Navigation
        return PendingIntent.getActivity(getApplicationContext(), 0, MainActivity.clearTop(this), 0);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    public class KeySetBinder extends Binder {
        public KeyCachingService getService() {
            return KeyCachingService.this;
        }
    }
}
