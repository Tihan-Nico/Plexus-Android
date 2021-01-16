package com.plexus.utils;

import android.content.Context;

public abstract class MediaConstraints {

    public abstract int getVideoMaxSize(Context context);

    public int getUncompressedVideoMaxSize(Context context) {
        return getVideoMaxSize(context);
    }

    public int getCompressedVideoMaxSize(Context context) {
        return getVideoMaxSize(context);
    }

}
