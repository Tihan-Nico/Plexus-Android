package com.plexus.database;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;

import com.plexus.database.helpers.SQLCipherOpenHelper;
import com.plexus.model.account.UserId;
import com.plexus.utils.CursorUtil;

import net.sqlcipher.Cursor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The backing datastore for {@link RemappedRecords}. See that class for more details.
 */
public class RemappedRecordsDatabase extends Database {

    public static final String[] CREATE_TABLE = { Recipients.CREATE_TABLE,
            Threads.CREATE_TABLE };

    private static class SharedColumns {
        protected static final String ID     = "_id";
        protected static final String OLD_ID = "old_id";
        protected static final String NEW_ID = "new_id";
    }

    private static final class Recipients extends SharedColumns {
        private static final String TABLE_NAME   = "remapped_recipients";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                OLD_ID + " INTEGER UNIQUE, " +
                NEW_ID + " INTEGER)";
    }

    private static final class Threads extends SharedColumns {
        private static final String TABLE_NAME   = "remapped_threads";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                OLD_ID + " INTEGER UNIQUE, " +
                NEW_ID + " INTEGER)";
    }

    RemappedRecordsDatabase(Context context, SQLCipherOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @NonNull
    Map<UserId, UserId> getAllRecipientMappings() {
        SQLiteDatabase                db           = databaseHelper.getReadableDatabase();
        Map<UserId, UserId> recipientMap = new HashMap<>();

        db.beginTransaction();
        try {
            List<Mapping> mappings = getAllMappings(Recipients.TABLE_NAME);

            for (Mapping mapping : mappings) {
                UserId oldId = UserId.from(mapping.getOldId());
                UserId newId = UserId.from(mapping.getNewId());
                recipientMap.put(oldId, newId);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return recipientMap;
    }

    @NonNull Map<String, String> getAllThreadMappings() {
        SQLiteDatabase  db        = databaseHelper.getReadableDatabase();
        Map<String, String> threadMap = new HashMap<>();

        db.beginTransaction();
        try {
            List<Mapping> mappings = getAllMappings(Threads.TABLE_NAME);

            for (Mapping mapping : mappings) {
                threadMap.put(mapping.getOldId(), mapping.getNewId());
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return threadMap;
    }

    void addRecipientMapping(@NonNull UserId oldId, @NonNull UserId newId) {
        addMapping(Recipients.TABLE_NAME, new Mapping(oldId.toString(), newId.toString()));
    }

    void addThreadMapping(String oldId, String newId) {
        addMapping(Threads.TABLE_NAME, new Mapping(oldId, newId));
    }

    private @NonNull List<Mapping> getAllMappings(@NonNull String table) {
        List<Mapping> mappings = new LinkedList<>();

        try (Cursor cursor = databaseHelper.getReadableDatabase().query(table, null, null, null, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                String oldId = CursorUtil.requireString(cursor, SharedColumns.OLD_ID);
                String newId = CursorUtil.requireString(cursor, SharedColumns.NEW_ID);
                mappings.add(new Mapping(oldId, newId));
            }
        }

        return mappings;
    }

    private void addMapping(@NonNull String table, @NonNull Mapping mapping) {
        ContentValues values = new ContentValues();
        values.put(SharedColumns.OLD_ID, mapping.getOldId());
        values.put(SharedColumns.NEW_ID, mapping.getNewId());

        databaseHelper.getWritableDatabase().insert(table, null, values);
    }

    static final class Mapping {
        private final String oldId;
        private final String newId;

        public Mapping(String oldId, String newId) {
            this.oldId = oldId;
            this.newId = newId;
        }

        public String getOldId() {
            return oldId;
        }

        public String getNewId() {
            return newId;
        }
    }
}

