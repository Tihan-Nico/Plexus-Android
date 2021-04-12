package com.plexus.migrations;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.plexus.crypto.MasterSecret;
import com.plexus.database.DatabaseFactory;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.jobmanagers.Data;
import com.plexus.jobmanagers.Job;
import com.plexus.jobs.CreateSignedPreKeyJob;
import com.plexus.services.KeyCachingService;
import com.plexus.transport.RetryLaterException;
import com.plexus.utils.FileUtils;
import com.plexus.utils.VersionTracker;
import com.plexus.utils.logging.Log;

import java.io.File;

/**
 * Represents all of the migrations that used to take place in {@link ApplicationMigrationActivity}
 * (previously known as DatabaseUpgradeActivity). This job should *never* have new versions or
 * migrations added to it. Instead, create a new {@link MigrationJob} and place it in
 * {@link ApplicationMigrations}.
 */
public class LegacyMigrationJob extends MigrationJob {

    public static final String KEY = "LegacyMigrationJob";
    public static final int NO_MORE_KEY_EXCHANGE_PREFIX_VERSION = 46;
    public static final int ASYMMETRIC_MASTER_SECRET_FIX_VERSION = 73;
    public static final int SQLCIPHER = 334;
    private static final String TAG = Log.tag(LegacyMigrationJob.class);
    private static final int CURVE25519_VERSION = 63;
    private static final int NO_V1_VERSION = 83;
    private static final int SIGNED_PREKEY_VERSION = 83;
    private static final int PERSISTENT_BLOBS = 317;
    private static final int SQLCIPHER_COMPLETE = 352;
    private static final int REMOVE_JOURNAL = 353;
    private static final int REMOVE_CACHE = 354;
    private static final int IMAGE_CACHE_CLEANUP = 406;


    public LegacyMigrationJob() {
        this(new Parameters.Builder().build());
    }

    private LegacyMigrationJob(@NonNull Parameters parameters) {
        super(parameters);
    }

    @Override
    public boolean isUiBlocking() {
        return true;
    }

    @Override
    public @NonNull
    String getFactoryKey() {
        return KEY;
    }

    @Override
    void performMigration() throws RetryLaterException {
        Log.i(TAG, "Running background upgrade..");
        int lastSeenVersion = VersionTracker.getLastSeenVersion(context);
        MasterSecret masterSecret = KeyCachingService.getMasterSecret(context);

        if (lastSeenVersion < SQLCIPHER && masterSecret != null) {
            DatabaseFactory.getInstance(context).onApplicationLevelUpgrade(context, masterSecret, lastSeenVersion, (progress, total) -> {
                Log.i(TAG, "onApplicationLevelUpgrade: " + progress + "/" + total);
            });
        } else if (lastSeenVersion < SQLCIPHER) {
            throw new RetryLaterException();
        }

        /*if (lastSeenVersion < CURVE25519_VERSION) {
            IdentityKeyUtil.migrateIdentityKeys(context, masterSecret);
        }*/

        if (lastSeenVersion < NO_V1_VERSION) {
            File v1sessions = new File(context.getFilesDir(), "sessions");

            if (v1sessions.exists() && v1sessions.isDirectory()) {
                File[] contents = v1sessions.listFiles();

                if (contents != null) {
                    for (File session : contents) {
                        session.delete();
                    }
                }

                v1sessions.delete();
            }
        }

        if (lastSeenVersion < SIGNED_PREKEY_VERSION) {
            PlexusDependencies.getJobManager().add(new CreateSignedPreKeyJob(context));
        }

        if (lastSeenVersion < PERSISTENT_BLOBS) {
            File externalDir = context.getExternalFilesDir(null);

            if (externalDir != null && externalDir.isDirectory() && externalDir.exists()) {
                for (File blob : externalDir.listFiles()) {
                    if (blob.exists() && blob.isFile()) blob.delete();
                }
            }
        }

        if (lastSeenVersion < SQLCIPHER_COMPLETE) {
            File file = context.getDatabasePath("messages.db");
            if (file != null && file.exists()) file.delete();
        }

        if (lastSeenVersion < REMOVE_JOURNAL) {
            File file = context.getDatabasePath("messages.db-journal");
            if (file != null && file.exists()) file.delete();
        }

        if (lastSeenVersion < REMOVE_CACHE) {
            FileUtils.deleteDirectoryContents(context.getCacheDir());
        }

        if (lastSeenVersion < IMAGE_CACHE_CLEANUP) {
            FileUtils.deleteDirectoryContents(context.getExternalCacheDir());
            Glide.get(context).clearDiskCache();
        }
    }

    @Override
    boolean shouldRetry(@NonNull Exception e) {
        return e instanceof RetryLaterException;
    }

    public interface DatabaseUpgradeListener {
        void setProgress(int progress, int total);
    }

    public static final class Factory implements Job.Factory<LegacyMigrationJob> {
        @Override
        public @NonNull
        LegacyMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
            return new LegacyMigrationJob(parameters);
        }
    }
}
