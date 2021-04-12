package com.plexus.database.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.plexus.crypto.MasterCipher;
import com.plexus.crypto.MasterSecret;
import com.plexus.crypto.MasterSecretUtil;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.migrations.LegacyMigrationJob;
import com.plexus.protocol.InvalidMessageException;
import com.plexus.utils.logging.Log;

public class ClassicOpenHelper extends SQLiteOpenHelper {

    static final String NAME = "conversation";

    private static final int INTRODUCED_DRAFTS_VERSION = 1;
    private static final int DATABASE_VERSION = 3;

    private static final String TAG = ClassicOpenHelper.class.getSimpleName();

    private final Context context;

    public ClassicOpenHelper(Context context) {
        super(context, NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void onApplicationLevelUpgrade(Context context, MasterSecret masterSecret, int fromVersion,
                                          LegacyMigrationJob.DatabaseUpgradeListener listener) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        if (fromVersion < LegacyMigrationJob.NO_MORE_KEY_EXCHANGE_PREFIX_VERSION) {
            String KEY_EXCHANGE = "?LookoutKeyExchange";
            String PROCESSED_KEY_EXCHANGE = "?LookoutKeyExchangd";
            String STALE_KEY_EXCHANGE = "?LookoutKeyExchangs";
            int ROW_LIMIT = 500;

            MasterCipher masterCipher = new MasterCipher(masterSecret);
            int smsCount = 0;
            int threadCount = 0;
            int skip = 0;

            Cursor cursor = db.query("sms", new String[]{"COUNT(*)"}, "type & " + 0x80000000 + " != 0",
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                smsCount = cursor.getInt(0);
                cursor.close();
            }

            cursor = db.query("thread", new String[]{"COUNT(*)"}, "snippet_type & " + 0x80000000 + " != 0",
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                threadCount = cursor.getInt(0);
                cursor.close();
            }

            Cursor smsCursor = null;

            Log.i(TAG, "Upgrade count: " + (smsCount + threadCount));

            do {
                Log.i(TAG, "Looping SMS cursor...");
                if (smsCursor != null)
                    smsCursor.close();

                smsCursor = db.query("sms", new String[]{"_id", "type", "body"},
                        "type & " + 0x80000000 + " != 0",
                        null, null, null, "_id", skip + "," + ROW_LIMIT);

                while (smsCursor != null && smsCursor.moveToNext()) {
                    listener.setProgress(smsCursor.getPosition() + skip, smsCount + threadCount);

                    try {
                        String body = masterCipher.decryptBody(smsCursor.getString(smsCursor.getColumnIndexOrThrow("body")));
                        long type = smsCursor.getLong(smsCursor.getColumnIndexOrThrow("type"));
                        long id = smsCursor.getLong(smsCursor.getColumnIndexOrThrow("_id"));

                        if (body.startsWith(KEY_EXCHANGE)) {
                            body = body.substring(KEY_EXCHANGE.length());
                            body = masterCipher.encryptBody(body);
                            type |= 0x8000;

                            db.execSQL("UPDATE sms SET body = ?, type = ? WHERE _id = ?",
                                    new String[]{body, type + "", id + ""});
                        } else if (body.startsWith(PROCESSED_KEY_EXCHANGE)) {
                            body = body.substring(PROCESSED_KEY_EXCHANGE.length());
                            body = masterCipher.encryptBody(body);
                            type |= (0x8000 | 0x2000);

                            db.execSQL("UPDATE sms SET body = ?, type = ? WHERE _id = ?",
                                    new String[]{body, type + "", id + ""});
                        } else if (body.startsWith(STALE_KEY_EXCHANGE)) {
                            body = body.substring(STALE_KEY_EXCHANGE.length());
                            body = masterCipher.encryptBody(body);
                            type |= (0x8000 | 0x4000);

                            db.execSQL("UPDATE sms SET body = ?, type = ? WHERE _id = ?",
                                    new String[]{body, type + "", id + ""});
                        }
                    } catch (InvalidMessageException e) {
                        Log.w(TAG, e);
                    }
                }

                skip += ROW_LIMIT;
            } while (smsCursor != null && smsCursor.getCount() > 0);


            Cursor threadCursor = null;
            skip = 0;

            do {
                Log.i(TAG, "Looping thread cursor...");

                if (threadCursor != null)
                    threadCursor.close();

                threadCursor = db.query("thread", new String[]{"_id", "snippet_type", "snippet"},
                        "snippet_type & " + 0x80000000 + " != 0",
                        null, null, null, "_id", skip + "," + ROW_LIMIT);

                while (threadCursor != null && threadCursor.moveToNext()) {
                    listener.setProgress(smsCount + threadCursor.getPosition(), smsCount + threadCount);

                    try {
                        String snippet = threadCursor.getString(threadCursor.getColumnIndexOrThrow("snippet"));
                        long snippetType = threadCursor.getLong(threadCursor.getColumnIndexOrThrow("snippet_type"));
                        long id = threadCursor.getLong(threadCursor.getColumnIndexOrThrow("_id"));

                        if (!TextUtils.isEmpty(snippet)) {
                            snippet = masterCipher.decryptBody(snippet);
                        }

                        if (snippet.startsWith(KEY_EXCHANGE)) {
                            snippet = snippet.substring(KEY_EXCHANGE.length());
                            snippet = masterCipher.encryptBody(snippet);
                            snippetType |= 0x8000;

                            db.execSQL("UPDATE thread SET snippet = ?, snippet_type = ? WHERE _id = ?",
                                    new String[]{snippet, snippetType + "", id + ""});
                        } else if (snippet.startsWith(PROCESSED_KEY_EXCHANGE)) {
                            snippet = snippet.substring(PROCESSED_KEY_EXCHANGE.length());
                            snippet = masterCipher.encryptBody(snippet);
                            snippetType |= (0x8000 | 0x2000);

                            db.execSQL("UPDATE thread SET snippet = ?, snippet_type = ? WHERE _id = ?",
                                    new String[]{snippet, snippetType + "", id + ""});
                        } else if (snippet.startsWith(STALE_KEY_EXCHANGE)) {
                            snippet = snippet.substring(STALE_KEY_EXCHANGE.length());
                            snippet = masterCipher.encryptBody(snippet);
                            snippetType |= (0x8000 | 0x4000);

                            db.execSQL("UPDATE thread SET snippet = ?, snippet_type = ? WHERE _id = ?",
                                    new String[]{snippet, snippetType + "", id + ""});
                        }
                    } catch (InvalidMessageException e) {
                        Log.w(TAG, e);
                    }
                }

                skip += ROW_LIMIT;
            } while (threadCursor != null && threadCursor.getCount() > 0);

            if (smsCursor != null)
                smsCursor.close();

            if (threadCursor != null)
                threadCursor.close();
        }

        if (fromVersion < LegacyMigrationJob.ASYMMETRIC_MASTER_SECRET_FIX_VERSION) {
            if (!MasterSecretUtil.hasAsymmericMasterSecret(context)) {
                MasterSecretUtil.generateAsymmetricMasterSecret(context, masterSecret);

            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        PlexusDependencies.getMessageNotifier().updateNotification(context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();

        if (oldVersion < INTRODUCED_DRAFTS_VERSION) {
            db.execSQL("CREATE TABLE drafts (_id INTEGER PRIMARY KEY, thread_id INTEGER, type TEXT, value TEXT);");
            executeStatements(db, new String[]{
                    "CREATE INDEX IF NOT EXISTS draft_thread_index ON drafts (thread_id);",
            });
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void executeStatements(SQLiteDatabase db, String[] statements) {
        for (String statement : statements)
            db.execSQL(statement);
    }

    private static class OldDirectoryDatabaseHelper extends SQLiteOpenHelper {

        private static final int INTRODUCED_CHANGE_FROM_TOKEN_TO_E164_NUMBER = 2;
        private static final int INTRODUCED_VOICE_COLUMN = 4;
        private static final int INTRODUCED_VIDEO_COLUMN = 5;

        private static final String DATABASE_NAME = "whisper_directory.db";
        private static final int DATABASE_VERSION = 5;

        private static final String TABLE_NAME = "directory";
        private static final String ID = "_id";
        private static final String NUMBER = "number";
        private static final String REGISTERED = "registered";
        private static final String RELAY = "relay";
        private static final String TIMESTAMP = "timestamp";
        private static final String VOICE = "voice";
        private static final String VIDEO = "video";

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
                NUMBER + " TEXT UNIQUE, " +
                REGISTERED + " INTEGER, " +
                RELAY + " TEXT, " +
                TIMESTAMP + " INTEGER, " +
                VOICE + " INTEGER, " +
                VIDEO + " INTEGER);";

        public OldDirectoryDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < INTRODUCED_CHANGE_FROM_TOKEN_TO_E164_NUMBER) {
                db.execSQL("DROP TABLE directory;");
                db.execSQL("CREATE TABLE directory ( _id INTEGER PRIMARY KEY, " +
                        "number TEXT UNIQUE, " +
                        "registered INTEGER, " +
                        "relay TEXT, " +
                        "supports_sms INTEGER, " +
                        "timestamp INTEGER);");
            }

            if (oldVersion < INTRODUCED_VOICE_COLUMN) {
                db.execSQL("ALTER TABLE directory ADD COLUMN voice INTEGER;");
            }

            if (oldVersion < INTRODUCED_VIDEO_COLUMN) {
                db.execSQL("ALTER TABLE directory ADD COLUMN video INTEGER;");
            }
        }
    }
}
