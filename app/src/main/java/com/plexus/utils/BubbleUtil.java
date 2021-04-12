package com.plexus.utils;

import com.plexus.core.utils.logging.Log;

/**
 * Bubble-related utility methods.
 */
public final class BubbleUtil {

    private static final String TAG = Log.tag(BubbleUtil.class);

    private BubbleUtil() {
    }

    public enum BubbleState {
        SHOWN,
        HIDDEN
    }
}
