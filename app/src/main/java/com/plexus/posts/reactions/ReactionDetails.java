package com.plexus.posts.reactions;

import androidx.annotation.NonNull;

import com.plexus.model.account.User;

public class ReactionDetails {
    private final User sender;
    private final String    baseEmoji;
    private final String    displayEmoji;
    private final long      timestamp;

    ReactionDetails(@NonNull User sender, @NonNull String baseEmoji, @NonNull String displayEmoji, long timestamp) {
        this.sender       = sender;
        this.baseEmoji    = baseEmoji;
        this.displayEmoji = displayEmoji;
        this.timestamp    = timestamp;
    }

    public @NonNull User getSender() {
        return sender;
    }

    public @NonNull String getBaseEmoji() {
        return baseEmoji;
    }

    public @NonNull String getDisplayEmoji() {
        return displayEmoji;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
