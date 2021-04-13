package com.plexus.providers;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class PartAuthority {

    private static final String AUTHORITY             = BuildConfig.APPLICATION_ID;
    private static final String PART_URI_STRING       = "content://" + AUTHORITY + "/part";
    private static final Uri    PART_CONTENT_URI      = Uri.parse(PART_URI_STRING);

    private static final int BLOB_ROW       = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BlobProvider.AUTHORITY, BlobProvider.PATH, BLOB_ROW);
    }

    public static InputStream getAttachmentThumbnailStream(@NonNull Context context, @NonNull Uri uri)
            throws IOException
    {
        return getAttachmentStream(context, uri);
    }

    public static InputStream getAttachmentStream(@NonNull Context context, @NonNull Uri uri)
            throws IOException
    {
        int match = uriMatcher.match(uri);
        try {
            switch (match) {
                case BLOB_ROW:       return BlobProvider.getInstance().getStream(context, uri);
                default:             return context.getContentResolver().openInputStream(uri);
            }
        } catch (SecurityException se) {
            throw new IOException(se);
        }
    }

    public static @Nullable String getAttachmentFileName(@NonNull Context context, @NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case BLOB_ROW:
                return BlobProvider.getFileName(uri);
            default:
                return null;
        }
    }

    public static @Nullable Long getAttachmentSize(@NonNull Context context, @NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case BLOB_ROW:
                return BlobProvider.getFileSize(uri);
            default:
                return null;
        }
    }

    public static @Nullable String getAttachmentContentType(@NonNull Context context, @NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case BLOB_ROW:
                return BlobProvider.getMimeType(uri);
            default:
                return null;
        }
    }

    public static boolean isLocalUri(final @NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BLOB_ROW:
                return true;
        }
        return false;
    }

}
