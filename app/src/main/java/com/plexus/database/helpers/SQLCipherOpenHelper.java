package com.plexus.database.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.plexus.crypto.DatabaseSecret;
import com.plexus.crypto.MasterSecret;
import com.plexus.database.LookoutDatabase;
import com.plexus.database.SqlCipherDatabaseHook;
import com.plexus.services.KeyCachingService;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.logging.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

public class SQLCipherOpenHelper extends SQLiteOpenHelper implements LookoutDatabase {

    @SuppressWarnings("unused")
    private static final String TAG = SQLCipherOpenHelper.class.getSimpleName();

    private static final int JOBMANAGER_STRIKES_BACK = 1;
    private static final int BLUR_HASH = 2;
    private static final int JOB_INPUT_DATA = 3;
    private static final int MIGRATE_PREKEYS_VERSION = 4;
    private static final int KEY_VALUE_STORE = 5;
    private static final int MEGAPHONES = 6;
    private static final int MEGAPHONE_FIRST_APPEARANCE = 7;
    private static final int PAYMENTS                         = 8;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "lookout.db";

    private final Context context;
    private final DatabaseSecret databaseSecret;

    public SQLCipherOpenHelper(@NonNull Context context, @NonNull DatabaseSecret databaseSecret) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, new SqlCipherDatabaseHook());

        this.context = context.getApplicationContext();
        this.databaseSecret = databaseSecret;
    }

    public static boolean databaseFileExists(@NonNull Context context) {
        return context.getDatabasePath(DATABASE_NAME).exists();
    }

    public static File getDatabaseFile(@NonNull Context context) {
        return context.getDatabasePath(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (context.getDatabasePath(ClassicOpenHelper.NAME).exists()) {
            ClassicOpenHelper legacyHelper = new ClassicOpenHelper(context);
            android.database.sqlite.SQLiteDatabase legacyDb = legacyHelper.getWritableDatabase();

            SQLCipherMigrationHelper.migratePlaintext(context, legacyDb, db);

            MasterSecret masterSecret = KeyCachingService.getMasterSecret(context);

            if (masterSecret != null)
                SQLCipherMigrationHelper.migrateCiphertext(context, masterSecret, legacyDb, db, null);
            else PlexusPreferences.setNeedsSqlCipherMigration(context, true);

            PreKeyMigrationHelper.cleanUpPreKeys(context);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database: " + oldVersion + ", " + newVersion);
        long startTime = System.currentTimeMillis();

        db.beginTransaction();

        try {

            if (oldVersion < JOBMANAGER_STRIKES_BACK) {
                db.execSQL("CREATE TABLE job_spec(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "job_spec_id TEXT UNIQUE, " +
                        "factory_key TEXT, " +
                        "queue_key TEXT, " +
                        "create_time INTEGER, " +
                        "next_run_attempt_time INTEGER, " +
                        "run_attempt INTEGER, " +
                        "max_attempts INTEGER, " +
                        "max_backoff INTEGER, " +
                        "max_instances INTEGER, " +
                        "lifespan INTEGER, " +
                        "serialized_data TEXT, " +
                        "is_running INTEGER)");

                db.execSQL("CREATE TABLE constraint_spec(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "job_spec_id TEXT, " +
                        "factory_key TEXT, " +
                        "UNIQUE(job_spec_id, factory_key))");

                db.execSQL("CREATE TABLE dependency_spec(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "job_spec_id TEXT, " +
                        "depends_on_job_spec_id TEXT, " +
                        "UNIQUE(job_spec_id, depends_on_job_spec_id))");
            }

            if (oldVersion < JOB_INPUT_DATA) {
                db.execSQL("ALTER TABLE job_spec ADD COLUMN serialized_input_data TEXT DEFAULT NULL");
            }

            if (oldVersion < BLUR_HASH) {
                db.execSQL("ALTER TABLE part ADD COLUMN blur_hash TEXT DEFAULT NULL");
            }

            if (oldVersion < KEY_VALUE_STORE) {
                db.execSQL("CREATE TABLE key_value (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "key TEXT UNIQUE, " +
                        "value TEXT, " +
                        "type INTEGER)");
            }

            if (oldVersion < MEGAPHONES) {
                db.execSQL("CREATE TABLE megaphone (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "event TEXT UNIQUE, " +
                        "seen_count INTEGER, " +
                        "last_seen INTEGER, " +
                        "finished INTEGER)");
            }

            if (oldVersion < MEGAPHONE_FIRST_APPEARANCE) {
                db.execSQL("ALTER TABLE megaphone ADD COLUMN first_visible INTEGER DEFAULT 0");
            }

            if (oldVersion < PAYMENTS) {
                db.execSQL("CREATE TABLE payments(_id INTEGER PRIMARY KEY, " +
                        "uuid TEXT DEFAULT NULL, " +
                        "recipient INTEGER DEFAULT 0, " +
                        "recipient_address TEXT DEFAULT NULL, " +
                        "timestamp INTEGER, " +
                        "note TEXT DEFAULT NULL, " +
                        "direction INTEGER, " +
                        "state INTEGER, " +
                        "failure_reason INTEGER, " +
                        "amount BLOB NOT NULL, " +
                        "fee BLOB NOT NULL, " +
                        "transaction_record BLOB DEFAULT NULL, " +
                        "receipt BLOB DEFAULT NULL, " +
                        "payment_metadata BLOB DEFAULT NULL, " +
                        "receipt_public_key TEXT DEFAULT NULL, " +
                        "block_index INTEGER DEFAULT 0, " +
                        "block_timestamp INTEGER DEFAULT 0, " +
                        "seen INTEGER, " +
                        "UNIQUE(uuid) ON CONFLICT ABORT)");

                db.execSQL("CREATE INDEX IF NOT EXISTS timestamp_direction_index ON payments (timestamp, direction);");
                db.execSQL("CREATE INDEX IF NOT EXISTS timestamp_index ON payments (timestamp);");
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS receipt_public_key_index ON payments (receipt_public_key);");
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (oldVersion < MIGRATE_PREKEYS_VERSION) {
            PreKeyMigrationHelper.cleanUpPreKeys(context);
        }

        Log.i(TAG, "Upgrade complete. Took " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    public com.plexus.database.SQLiteDatabase getReadableDatabase() {
        return new com.plexus.database.SQLiteDatabase(getReadableDatabase(databaseSecret.asString()));
    }

    public com.plexus.database.SQLiteDatabase getWritableDatabase() {
        return new com.plexus.database.SQLiteDatabase(getWritableDatabase(databaseSecret.asString()));
    }

    @Override
    public @NonNull
    SQLiteDatabase getSqlCipherDatabase() {
        return getWritableDatabase().getSqlCipherDatabase();
    }

    public void markCurrent(SQLiteDatabase db) {
        db.setVersion(DATABASE_VERSION);
    }

    private void executeStatements(SQLiteDatabase db, String[] statements) {
        for (String statement : statements)
            db.execSQL(statement);
    }
}
