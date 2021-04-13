package com.plexus.keyvalue;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsValue extends PlexusStoreValues {

    private static final String PLEXUS_BACKUP_DIRECTORY = "settings.plexus.backup.directory";
    private static final String PLEXUS_LATEST_BACKUP_DIRECTORY = "settings.plexus.backup.directory,latest";

    private static final String PLEXUS_EXPERIMENTAL_NOTICES = "settings.plexus.experimental.notices";
    private static final String PLEXUS_EXPERIMENTAL_VERSION = "settings.plexus.experimental.version";

    SettingsValue(@NonNull @NotNull KeyValueStore store) {
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

    public void setPlexusBackupDirectory(@NonNull Uri uri) {
        putString(PLEXUS_BACKUP_DIRECTORY, uri.toString());
        putString(PLEXUS_LATEST_BACKUP_DIRECTORY, uri.toString());
    }

    public void setPlexusExperimentalValues(@NonNull boolean values) {
        putBoolean(PLEXUS_EXPERIMENTAL_NOTICES, values);
        putBoolean(PLEXUS_EXPERIMENTAL_VERSION, values);
    }

    public @Nullable
    Uri getPlexusBackupDirectory() {
        return getUri(PLEXUS_BACKUP_DIRECTORY);
    }

    public @Nullable
    Uri getLatestPlexusBackupDirectory() {
        return getUri(PLEXUS_LATEST_BACKUP_DIRECTORY);
    }

    public void clearPlexusBackupDirectory() {
        putString(PLEXUS_BACKUP_DIRECTORY, null);
    }

    public @Nullable
    boolean getExperimentalNotices() {
        return getBoolean(PLEXUS_EXPERIMENTAL_NOTICES, false);
    }

    public @Nullable
    boolean getExperimentalVersion() {
        return getBoolean(PLEXUS_EXPERIMENTAL_VERSION, false);
    }


    private @Nullable
    Uri getUri(@NonNull String key) {
        String uri = getString(key, "");

        if (TextUtils.isEmpty(uri)) {
            return null;
        } else {
            return Uri.parse(uri);
        }
    }

}
