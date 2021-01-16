package com.plexus.utils;

import android.content.Context;

public abstract class MediaConstraints {

    public abstract int getImageMaxWidth(Context context);
    public abstract int getImageMaxHeight(Context context);
    public abstract int getImageMaxSize(Context context);

    public abstract int getVideoMaxSize(Context context);

    public int getUncompressedVideoMaxSize(Context context) {
        return getVideoMaxSize(context);
    }

    public int getCompressedVideoMaxSize(Context context) {
        return getVideoMaxSize(context);
    }

}
