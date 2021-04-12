package com.plexus.keyvalue;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public final class MiscellaneousValues extends PlexusStoreValues {

    private static final String LAST_PREKEY_REFRESH_TIME        = "last_prekey_refresh_time";
    private static final String LAST_PROFILE_REFRESH_TIME       = "misc.last_profile_refresh_time";
    private static final String CLIENT_DEPRECATED               = "misc.client_deprecated";
    private static final String OLD_DEVICE_TRANSFER_LOCKED      = "misc.old_device.transfer.locked";

    MiscellaneousValues(@NonNull KeyValueStore store) {
        super(store);
    }

    @Override
    void onFirstEverAppLaunch() {
    }

    @Override
    @NonNull
    List<String> getKeysToIncludeInBackup() {
        return Collections.emptyList();
    }

    public long getLastPrekeyRefreshTime() {
        return getLong(LAST_PREKEY_REFRESH_TIME, 0);
    }

    public void setLastPrekeyRefreshTime(long time) {
        putLong(LAST_PREKEY_REFRESH_TIME, time);
    }

    public long getLastProfileRefreshTime() {
        return getLong(LAST_PROFILE_REFRESH_TIME, 0);
    }

    public void setLastProfileRefreshTime(long time) {
        putLong(LAST_PROFILE_REFRESH_TIME, time);
    }

    public boolean isClientDeprecated() {
        return getBoolean(CLIENT_DEPRECATED, false);
    }

    public void markClientDeprecated() {
        putBoolean(CLIENT_DEPRECATED, true);
    }

    public void clearClientDeprecated() {
        putBoolean(CLIENT_DEPRECATED, false);
    }

    public boolean isOldDeviceTransferLocked() {
        return getBoolean(OLD_DEVICE_TRANSFER_LOCKED, false);
    }

    public void markOldDeviceTransferLocked() {
        putBoolean(OLD_DEVICE_TRANSFER_LOCKED, true);
    }

    public void clearOldDeviceTransferLocked() {
        putBoolean(OLD_DEVICE_TRANSFER_LOCKED, false);
    }
}
