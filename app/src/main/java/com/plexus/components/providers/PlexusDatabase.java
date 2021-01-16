package com.plexus.components.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlexusDatabase extends SQLiteOpenHelper {

    public static final String DATABASENAME = "Plexus.db";
    private static final int VERSION = 4;
    private static PlexusDatabase sInstance = null;

    private final Context mContext;

    public PlexusDatabase(final Context context) {
        super(context, DATABASENAME, null, VERSION);

        mContext = context;
    }

    public static final synchronized PlexusDatabase getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PlexusDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SearchDatabase.getInstance(mContext).onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SearchDatabase.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SearchDatabase.getInstance(mContext).onDowngrade(db, oldVersion, newVersion);
    }

}
