package com.plexus.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.plexus.crypto.DatabaseSecret;
import com.plexus.crypto.DatabaseSecretProvider;
import com.plexus.crypto.MasterSecret;
import com.plexus.database.helpers.ClassicOpenHelper;
import com.plexus.database.helpers.SQLCipherMigrationHelper;
import com.plexus.database.helpers.SQLCipherOpenHelper;
import com.plexus.migrations.LegacyMigrationJob;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.SqlUtil;

import net.sqlcipher.database.SQLiteDatabase;

public class DatabaseFactory {

    private static final Object lock = new Object();

    private static volatile DatabaseFactory instance;

    private final SQLCipherOpenHelper databaseHelper;

    private DatabaseFactory(@NonNull Context context) {
        SQLiteDatabase.loadLibs(context);

        DatabaseSecret databaseSecret = DatabaseSecretProvider.getOrCreateDatabaseSecret(context);

        this.databaseHelper = new SQLCipherOpenHelper(context, databaseSecret);
    }

    public static DatabaseFactory getInstance(Context context) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseFactory(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public static SQLiteDatabase getBackupDatabase(Context context) {
        return getInstance(context).databaseHelper.getReadableDatabase().getSqlCipherDatabase();
    }

    public static void upgradeRestored(Context context, SQLiteDatabase database) {
        synchronized (lock) {
            getInstance(context).databaseHelper.onUpgrade(database, database.getVersion(), -1);
            getInstance(context).databaseHelper.markCurrent(database);
            getInstance(context).getRawDatabase().rawExecSQL("DROP TABLE IF EXISTS key_value");
            getInstance(context).getRawDatabase().rawExecSQL("DROP TABLE IF EXISTS megaphone");
            getInstance(context).getRawDatabase().rawExecSQL("DROP TABLE IF EXISTS job_spec");
            getInstance(context).getRawDatabase().rawExecSQL("DROP TABLE IF EXISTS constraint_spec");
            getInstance(context).getRawDatabase().rawExecSQL("DROP TABLE IF EXISTS dependency_spec");

            instance.databaseHelper.close();
            instance = null;
        }
    }

    public static boolean inTransaction(Context context) {
        return getInstance(context).databaseHelper.getWritableDatabase().inTransaction();
    }

    public void onApplicationLevelUpgrade(@NonNull Context context, @NonNull MasterSecret masterSecret,
                                          int fromVersion, LegacyMigrationJob.DatabaseUpgradeListener listener) {
        databaseHelper.getWritableDatabase();

        ClassicOpenHelper legacyOpenHelper = null;

        if (fromVersion < LegacyMigrationJob.ASYMMETRIC_MASTER_SECRET_FIX_VERSION) {
            legacyOpenHelper = new ClassicOpenHelper(context);
            legacyOpenHelper.onApplicationLevelUpgrade(context, masterSecret, fromVersion, listener);
        }

        if (fromVersion < LegacyMigrationJob.SQLCIPHER && PlexusPreferences.getNeedsSqlCipherMigration(context)) {
            if (legacyOpenHelper == null) {
                legacyOpenHelper = new ClassicOpenHelper(context);
            }

            SQLCipherMigrationHelper.migrateCiphertext(context, masterSecret,
                    legacyOpenHelper.getWritableDatabase(),
                    databaseHelper.getWritableDatabase().getSqlCipherDatabase(),
                    listener);
        }
    }

    public void triggerDatabaseAccess() {
        databaseHelper.getWritableDatabase();
    }

    public SQLiteDatabase getRawDatabase() {
        return databaseHelper.getWritableDatabase().getSqlCipherDatabase();
    }

    public boolean hasTable(String table) {
        return SqlUtil.tableExists(databaseHelper.getReadableDatabase().getSqlCipherDatabase(), table);
    }
}