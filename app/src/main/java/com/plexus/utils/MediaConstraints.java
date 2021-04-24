package com.plexus.utils;

import android.content.Context;
import android.os.Build;

import com.plexus.media.PushMediaConstraints;

public abstract class MediaConstraints {
    private static final String TAG = MediaConstraints.class.getSimpleName();

    public static MediaConstraints getPushMediaConstraints() {
        return new PushMediaConstraints();
    }

    public abstract int getImageMaxWidth(Context context);
    public abstract int getImageMaxHeight(Context context);
    public abstract int getImageMaxSize(Context context);

    /**
     * Provide a list of dimensions that should be attempted during compression. We will keep moving
     * down the list until the image can be scaled to fit under {@link #getImageMaxSize(Context)}.
     * The first entry in the list should match your max width/height.
     */
    public abstract int[] getImageDimensionTargets(Context context);

    public abstract int getGifMaxSize(Context context);
    public abstract int getVideoMaxSize(Context context);

    public int getUncompressedVideoMaxSize(Context context) {
        return getVideoMaxSize(context);
    }

    public int getCompressedVideoMaxSize(Context context) {
        return getVideoMaxSize(context);
    }

    public abstract int getAudioMaxSize(Context context);
    public abstract int getDocumentMaxSize(Context context);

    public static boolean isVideoTranscodeAvailable() {
        return Build.VERSION.SDK_INT >= 26 && (FeatureFlags.useStreamingVideoMuxer() || MemoryFileDescriptor.supported());
    }
}
