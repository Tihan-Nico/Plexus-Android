package com.plexus.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BlobProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".blob";
    public static final String PATH = "blob/*/*/*/*/*";

    private static final int FILESIZE_PATH_SEGMENT     = 4;

    private static final int MATCH = 1;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH) {{
        addURI(AUTHORITY, PATH, MATCH);
    }};

    private static final BlobProvider INSTANCE = new BlobProvider();

    public static BlobProvider getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieve a stream for the content with the specified URI.
     * @throws IOException If the stream fails to open or the spec of the URI doesn't match.
     */
    public synchronized @NonNull InputStream getStream(@NonNull Context context, @NonNull Uri uri) throws IOException {
        return getStream(context, uri, 0L);
    }

    /**
     * Retrieve a stream for the content with the specified URI starting from the specified position.
     * @throws IOException If the stream fails to open or the spec of the URI doesn't match.
     */
    public synchronized @NonNull InputStream getStream(@NonNull Context context, @NonNull Uri uri, long position) throws IOException {
        return getBlobRepresentation(context,
                uri,
                bytes -> {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    if (byteArrayInputStream.skip(position) != position) {
                        throw new IOException("Failed to skip to position " + position + " for: " + uri);
                    }
                    return byteArrayInputStream;
                },
                file -> ModernDecryptingPartInputStream.createFor(getAttachmentSecret(context),
                        file,
                        position));
    }

    public static @Nullable
    Long getFileSize(@NonNull Uri uri) {
        if (isAuthority(uri)) {
            try {
                return Long.parseLong(uri.getPathSegments().get(FILESIZE_PATH_SEGMENT));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static boolean isAuthority(@NonNull Uri uri) {
        return URI_MATCHER.match(uri) == MATCH;
    }

}
