package com.plexus.database;

import net.sqlcipher.database.SQLiteDatabase;

public interface LookoutDatabase {
    SQLiteDatabase getSqlCipherDatabase();

    String getDatabaseName();
}
