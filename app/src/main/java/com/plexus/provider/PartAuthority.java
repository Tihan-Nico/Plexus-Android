package com.plexus.provider;

import android.content.UriMatcher;
import android.net.Uri;

import androidx.annotation.NonNull;

import static com.plexus.provider.BlobProvider.AUTHORITY;

public class PartAuthority {

    private static final int PART_ROW       = 1;
    private static final int PERSISTENT_ROW = 2;
    private static final int BLOB_ROW       = 3;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "part/*/#", PART_ROW);
        uriMatcher.addURI(AUTHORITY, BlobProvider.PATH, BLOB_ROW);
    }

    public static boolean isLocalUri(final @NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PART_ROW:
            case PERSISTENT_ROW:
            case BLOB_ROW:
                return true;
        }
        return false;
    }

}
