package com.plexus.keyvalue;

import androidx.annotation.NonNull;

import com.plexus.dependecies.PlexusDependencies;

import java.util.ArrayList;
import java.util.List;

public class PlexusStore {

    private static final PlexusStore INSTANCE = new PlexusStore();

    private final KeyValueStore store;
    private final MiscellaneousValues misc;
    private final EmojiValues emojiValues;

    private PlexusStore() {
        this.store = new KeyValueStore(PlexusDependencies.getApplication());
        this.misc = new MiscellaneousValues(store);
        this.emojiValues = new EmojiValues(store);
    }

    public static void onFirstEverAppLaunch() {
        misc().onFirstEverAppLaunch();
        emojiValues().onFirstEverAppLaunch();
    }

    public static List<String> getKeysToIncludeInBackup() {
        List<String> keys = new ArrayList<>();
        keys.addAll(misc().getKeysToIncludeInBackup());
        keys.addAll(emojiValues().getKeysToIncludeInBackup());
        return keys;
    }

    public static @NonNull
    MiscellaneousValues misc() {
        return INSTANCE.misc;
    }

    public static @NonNull
    EmojiValues emojiValues() {
        return INSTANCE.emojiValues;
    }


    /**
     * Ensures any pending writes are finished.
     */
    public static void blockUntilAllWritesFinished() {
        getStore().blockUntilAllWritesFinished();
    }

    private static @NonNull
    KeyValueStore getStore() {
        return INSTANCE.store;
    }

}
