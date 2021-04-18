package com.plexus.keyvalue;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * These values are only used to store profile settings offline
 * instead of fetching them from the database every single time.
 * When a user updates their profile settings it will be saved on
 * the database and in the KeyValue database.
 */

public class ProfileValues extends PlexusStoreValues{

    private static final String PROFILE_PRIVACY = "profile_privacy";

    ProfileValues(@NonNull @NotNull KeyValueStore store) {
        super(store);
    }

    @Override
    void onFirstEverAppLaunch() {

    }

    @NonNull
    @NotNull
    @Override
    List<String> getKeysToIncludeInBackup() {
        return null;
    }
}
