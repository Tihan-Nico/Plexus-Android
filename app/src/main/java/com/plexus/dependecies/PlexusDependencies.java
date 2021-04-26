package com.plexus.dependecies;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.plexus.database.DatabaseObserver;
import com.plexus.jobmanagers.JobManager;
import com.plexus.megaphone.MegaphoneRepository;
import com.plexus.model.account.LiveUserCache;
import com.plexus.net.ContentProxySelector;
import com.plexus.net.StandardUserAgentInterceptor;
import com.plexus.notifications.MessageNotifier;
import com.plexus.shakereport.ShakeToReport;
import com.plexus.utils.AppForegroundObserver;
import com.plexus.utils.FrameRateTracker;

import okhttp3.OkHttpClient;

/**
 * Location for storing and retrieving application-scoped singletons. Users must call
 * {@link #init(Application, Provider)} before using any of the methods, preferably early on in
 * {@link Application#onCreate()}.
 * <p>
 * All future application-scoped singletons should be written as normal objects, then placed here
 * to manage their singleton-ness.
 */
public class PlexusDependencies {

    private static final Object LOCK = new Object();
    private static final Object FRAME_RATE_TRACKER_LOCK = new Object();
    private static final Object JOB_MANAGER_LOCK = new Object();

    private static Application application;
    private static Provider provider;
    private static MessageNotifier messageNotifier;
    private static AppForegroundObserver appForegroundObserver;

    private static volatile MegaphoneRepository megaphoneRepository;
    private static volatile JobManager jobManager;
    private static volatile FrameRateTracker frameRateTracker;
    private static volatile DatabaseObserver databaseObserver;
    private static volatile ShakeToReport shakeToReport;
    private static volatile OkHttpClient okHttpClient;
    private static volatile LiveUserCache recipientCache;

    @MainThread
    public static void init(@NonNull Application application, @NonNull Provider provider) {
        synchronized (LOCK) {
            if (PlexusDependencies.application != null || PlexusDependencies.provider != null) {
                throw new IllegalStateException("Already initialized!");
            }

            PlexusDependencies.application = application;
            PlexusDependencies.provider = provider;
            PlexusDependencies.messageNotifier = provider.provideMessageNotifier();
            PlexusDependencies.appForegroundObserver = provider.provideAppForegroundObserver();

            PlexusDependencies.appForegroundObserver.begin();
        }
    }

    public static @NonNull
    Application getApplication() {
        return application;
    }

    public static @NonNull
    MegaphoneRepository getMegaphoneRepository() {
        if (megaphoneRepository == null) {
            synchronized (LOCK) {
                if (megaphoneRepository == null) {
                    megaphoneRepository = provider.provideMegaphoneRepository();
                }
            }
        }

        return megaphoneRepository;
    }

    public static @NonNull
    JobManager getJobManager() {
        if (jobManager == null) {
            synchronized (JOB_MANAGER_LOCK) {
                if (jobManager == null) {
                    jobManager = provider.provideJobManager();
                }
            }
        }

        return jobManager;
    }

    public static @NonNull
    FrameRateTracker getFrameRateTracker() {
        if (frameRateTracker == null) {
            synchronized (FRAME_RATE_TRACKER_LOCK) {
                if (frameRateTracker == null) {
                    frameRateTracker = provider.provideFrameRateTracker();
                }
            }
        }

        return frameRateTracker;
    }

    public static @NonNull
    MessageNotifier getMessageNotifier() {
        return messageNotifier;
    }

    public static @NonNull
    DatabaseObserver getDatabaseObserver() {
        if (databaseObserver == null) {
            synchronized (LOCK) {
                if (databaseObserver == null) {
                    databaseObserver = provider.provideDatabaseObserver();
                }
            }
        }

        return databaseObserver;
    }

    public static @NonNull
    ShakeToReport getShakeToReport() {
        if (shakeToReport == null) {
            synchronized (LOCK) {
                if (shakeToReport == null) {
                    shakeToReport = provider.provideShakeToReport();
                }
            }
        }

        return shakeToReport;
    }

    public static @NonNull OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (LOCK) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .proxySelector(new ContentProxySelector())
                            .addInterceptor(new StandardUserAgentInterceptor())
                            .build();
                }
            }
        }

        return okHttpClient;
    }

    public static @NonNull LiveUserCache getRecipientCache() {
        if (recipientCache == null) {
            synchronized (LOCK) {
                if (recipientCache == null) {
                    recipientCache = provider.provideRecipientCache();
                }
            }
        }

        return recipientCache;
    }

    public static @NonNull
    AppForegroundObserver getAppForegroundObserver() {
        return appForegroundObserver;
    }


    public interface Provider {

        @NonNull
        MegaphoneRepository provideMegaphoneRepository();

        @NonNull
        JobManager provideJobManager();

        @NonNull
        FrameRateTracker provideFrameRateTracker();

        @NonNull
        ShakeToReport provideShakeToReport();

        @NonNull
        MessageNotifier provideMessageNotifier();

        @NonNull
        DatabaseObserver provideDatabaseObserver();

        @NonNull
        AppForegroundObserver provideAppForegroundObserver();

        @NonNull
        LiveUserCache provideRecipientCache();
    }

}
