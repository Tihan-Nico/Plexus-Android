package com.plexus.giph.mp4;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import com.plexus.core.utils.concurrent.PlexusExecutors;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.giph.model.GiphyImage;
import com.plexus.net.ContentProxySelector;
import com.plexus.net.StandardUserAgentInterceptor;
import com.plexus.providers.BlobProvider;
import com.plexus.utils.MediaUtil;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Repository responsible for downloading gifs selected by the user in the appropriate format.
 */
final class GiphyMp4Repository {

    private static final Executor EXECUTOR = PlexusExecutors.BOUNDED;

    private final OkHttpClient client;

    GiphyMp4Repository() {
        this.client = new OkHttpClient.Builder().proxySelector(new ContentProxySelector())
                .addInterceptor(new StandardUserAgentInterceptor())
                .build();
    }

    void saveToBlob(@NonNull GiphyImage giphyImage, boolean isForMms, @NonNull Consumer<GiphyMp4SaveResult> resultConsumer) {
        EXECUTOR.execute(() -> {
            try {
                Uri blob = saveToBlobInternal(giphyImage, isForMms);
                resultConsumer.accept(new GiphyMp4SaveResult.Success(blob, giphyImage));
            } catch (IOException e) {
                resultConsumer.accept(new GiphyMp4SaveResult.Error(e));
            }
        });
    }

    @WorkerThread
    private @NonNull
    Uri saveToBlobInternal(@NonNull GiphyImage giphyImage, boolean isForMms) throws IOException {
        boolean sendAsMp4 = GiphyMp4PlaybackPolicy.sendAsMp4();
        String url;
        String mime;

        if (sendAsMp4) {
            url = giphyImage.getMp4Url();
            mime = MediaUtil.VIDEO_MP4;
        } else if (isForMms) {
            url = giphyImage.getGifMmsUrl();
            mime = MediaUtil.IMAGE_GIF;
        } else {
            url = giphyImage.getGifUrl();
            mime = MediaUtil.IMAGE_GIF;
        }

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() >= 200 && response.code() < 300) {
                return BlobProvider.getInstance()
                        .forData(response.body().byteStream(), response.body().contentLength())
                        .withMimeType(mime)
                        .createForSingleSessionOnDisk(PlexusDependencies.getApplication());
            } else {
                throw new IOException("Unexpected response code: " + response.code());
            }
        }
    }
}
