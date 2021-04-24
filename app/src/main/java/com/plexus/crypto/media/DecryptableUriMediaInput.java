package com.plexus.crypto.media;

import android.content.Context;
import android.media.MediaDataSource;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.plexus.media.MediaInput;
import com.plexus.providers.BlobProvider;
import com.plexus.providers.PartAuthority;

import java.io.IOException;

@RequiresApi(api = 23)
public final class DecryptableUriMediaInput {

    private DecryptableUriMediaInput() {
    }

    public static @NonNull
    MediaInput createForUri(@NonNull Context context, @NonNull Uri uri) throws IOException {

        if (BlobProvider.isAuthority(uri)) {
            return new MediaInput.MediaDataSourceMediaInput(BlobProvider.getInstance().getMediaDataSource(context, uri));
        }

        if (PartAuthority.isLocalUri(uri)) {
            return createForAttachmentUri(context, uri);
        }

        return new MediaInput.UriMediaInput(context, uri);
    }

    private static @NonNull MediaInput createForAttachmentUri(@NonNull Context context, @NonNull Uri uri) {
        /*AttachmentId partId = new PartUriParser(uri).getPartId();

        if (!partId.isValid()) {
            throw new AssertionError();
        }*/

        MediaDataSource mediaDataSource = null; /*= DatabaseFactory.getAttachmentDatabase(context)
                .mediaDataSourceFor(partId);*/

        if (mediaDataSource == null) {
            throw new AssertionError();
        }

        return new MediaInput.MediaDataSourceMediaInput(mediaDataSource);
    }
}
