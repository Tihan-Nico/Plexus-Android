package com.plexus.components.components.socials.commons;

import androidx.annotation.NonNull;

/**
 * Abstract hashtag to be used with {@link HashtagArrayAdapter}.
 */
public interface Hashtagable {

    /**
     * Unique id of this hashtag.
     */
    @NonNull
    CharSequence getId();

    /**
     * Optional count, located right to hashtag name.
     */
    int getCount();
}