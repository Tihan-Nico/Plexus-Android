package com.plexus.providers;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

public class PartAuthority {

    private static final int BLOB_ROW       = 1;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BlobProvider.AUTHORITY, BlobProvider.PATH, BLOB_ROW);
    }

    public static InputStream getAttachmentStream(@NonNull Context context, @NonNull Uri uri)
            throws IOException
    {
        int match = uriMatcher.match(uri);
        try {
            if (match == BLOB_ROW) {
                return BlobProvider.getInstance().getStream(context, uri);
            }
            return context.getContentResolver().openInputStream(uri);
        } catch (SecurityException se) {
            throw new IOException(se);
        }
    }

}
