package com.plexus.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.annimon.stream.Stream;
import com.plexus.BuildConfig;
import com.plexus.core.utils.logging.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * A location for flags that can be set locally and remotely. These flags can guard features that
 * are not yet ready to be activated.
 * <p>
 * When creating a new flag:
 * - Create a new string constant. This should almost certainly be prefixed with "android."
 * - Add a method to retrieve the value using {@link (String)}. You can also add
 * other checks here, like requiring other flags.
 * - If you want to be able to change a flag remotely, place it in {@link #REMOTE_CAPABLE}.
 * - If you would like to force a value for testing, place an entry in {@link #FORCED_VALUES}.
 * Do not commit changes to this map!
 * <p>
 * Other interesting things you can do:
 * - Make a flag {@link #HOT_SWAPPABLE}
 */
public final class FeatureFlags {

    private static final String PAYMENTS_KILL_SWITCH              = "android.payments.kill";

    @VisibleForTesting
    static final Set<String> NOT_REMOTE_CAPABLE = SetUtil.newHashSet(
    );
    /**
     * Values in this map will take precedence over any value. This should only be used for local
     * development. Given that you specify a default when retrieving a value, and that we only store
     * remote values for things in {@link #REMOTE_CAPABLE}, there should be no need to ever *commit*
     * an addition to this map.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @VisibleForTesting
    static final Map<String, Object> FORCED_VALUES = new HashMap<String, Object>() {{
    }};
    private static final String TAG = Log.tag(FeatureFlags.class);
    private static final String CLIENT_EXPIRATION = "android.clientExpiration";
    /**
     * We will only store remote values for flags in this set. If you want a flag to be controllable
     * remotely, place it in here.
     */
    @VisibleForTesting
    static final Set<String> REMOTE_CAPABLE = SetUtil.newHashSet(
            PAYMENTS_KILL_SWITCH,
            CLIENT_EXPIRATION
    );
    /**
     * By default, flags are only updated once at app start. This is to ensure that values don't
     * change within an app session, simplifying logic. However, given that this can delay how often
     * a flag is updated, you can put a flag in here to mark it as 'hot swappable'. Flags in this set
     * will be updated arbitrarily at runtime. This will make values more responsive, but also places
     * more burden on the reader to ensure that the app experience remains consistent.
     */
    @VisibleForTesting
    static final Set<String> HOT_SWAPPABLE = SetUtil.newHashSet(
            CLIENT_EXPIRATION
    );
    private static final String INTERNAL_USER = "android.internalUser";
    private static final String DEFAULT_MAX_BACKOFF = "android.defaultMaxBackoff";
    private static final Map<String, Object> REMOTE_VALUES = new TreeMap<>();

    private FeatureFlags() {
    }

    public static synchronized void init() {

        Log.i(TAG, "init() " + REMOTE_VALUES.toString());
    }

    /**
     * The raw client expiration JSON string.
     */
    public static String clientExpiration() {
        return getString(CLIENT_EXPIRATION, null);
    }

    @VisibleForTesting
    static @NonNull
    UpdateResult updateInternal(@NonNull Map<String, Object> remote,
                                @NonNull Map<String, Object> localMemory,
                                @NonNull Map<String, Object> localDisk,
                                @NonNull Set<String> remoteCapable,
                                @NonNull Set<String> hotSwap,
                                @NonNull Set<String> sticky) {
        Map<String, Object> newMemory = new TreeMap<>(localMemory);
        Map<String, Object> newDisk = new TreeMap<>(localDisk);

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(remote.keySet());
        allKeys.addAll(localDisk.keySet());
        allKeys.addAll(localMemory.keySet());

        Stream.of(allKeys)
                .filter(remoteCapable::contains)
                .forEach(key -> {
                    Object remoteValue = remote.get(key);
                    Object diskValue = localDisk.get(key);
                    Object newValue = remoteValue;

                    if (newValue != null && diskValue != null && newValue.getClass() != diskValue.getClass()) {
                        Log.w(TAG, "Type mismatch! key: " + key);

                        newDisk.remove(key);

                        if (hotSwap.contains(key)) {
                            newMemory.remove(key);
                        }

                        return;
                    }

                    if (sticky.contains(key) && (newValue instanceof Boolean || diskValue instanceof Boolean)) {
                        newValue = diskValue == Boolean.TRUE ? Boolean.TRUE : newValue;
                    } else if (sticky.contains(key)) {
                        Log.w(TAG, "Tried to make a non-boolean sticky! Ignoring. (key: " + key + ")");
                    }

                    if (newValue != null) {
                        newDisk.put(key, newValue);
                    } else {
                        newDisk.remove(key);
                    }

                    if (hotSwap.contains(key)) {
                        if (newValue != null) {
                            newMemory.put(key, newValue);
                        } else {
                            newMemory.remove(key);
                        }
                    }
                });

        Stream.of(allKeys)
                .filterNot(remoteCapable::contains)
                .filterNot(key -> sticky.contains(key) && localDisk.get(key) == Boolean.TRUE)
                .forEach(key -> {
                    newDisk.remove(key);

                    if (hotSwap.contains(key)) {
                        newMemory.remove(key);
                    }
                });

        return new UpdateResult(newMemory, newDisk, computeChanges(localMemory, newMemory));
    }

    @VisibleForTesting
    static @NonNull
    Map<String, Change> computeChanges(@NonNull Map<String, Object> oldMap, @NonNull Map<String, Object> newMap) {
        Map<String, Change> changes = new HashMap<>();
        Set<String> allKeys = new HashSet<>();

        allKeys.addAll(oldMap.keySet());
        allKeys.addAll(newMap.keySet());

        for (String key : allKeys) {
            Object oldValue = oldMap.get(key);
            Object newValue = newMap.get(key);

            if (oldValue == null && newValue == null) {
                throw new AssertionError("Should not be possible.");
            } else if (oldValue != null && newValue == null) {
                changes.put(key, Change.REMOVED);
            } else if (newValue != oldValue && newValue instanceof Boolean) {
                changes.put(key, (boolean) newValue ? Change.ENABLED : Change.DISABLED);
            } else if (!Objects.equals(oldValue, newValue)) {
                changes.put(key, Change.CHANGED);
            }
        }

        return changes;
    }

    private static @NonNull
    VersionFlag getVersionFlag(@NonNull String key) {
        int versionFromKey = getInteger(key, 0);

        if (versionFromKey == 0) {
            return VersionFlag.OFF;
        }

        if (BuildConfig.CANONICAL_VERSION_CODE >= versionFromKey) {
            return VersionFlag.ON;
        } else {
            return VersionFlag.ON_IN_FUTURE_VERSION;
        }
    }

    /** Payments Support */
    public static boolean payments() {
        return !getBoolean(PAYMENTS_KILL_SWITCH, false);
    }

    /**
     * Internal testing extensions.
     */
    public static boolean internalUser() {
        return getBoolean(INTERNAL_USER, false);
    }

    private static boolean getBoolean(@NonNull String key, boolean defaultValue) {
        Boolean forced = (Boolean) FORCED_VALUES.get(key);
        if (forced != null) {
            return forced;
        }

        Object remote = REMOTE_VALUES.get(key);
        if (remote instanceof Boolean) {
            return (boolean) remote;
        } else if (remote != null) {
            Log.w(TAG, "Expected a boolean for key '" + key + "', but got something else! Falling back to the default.");
        }

        return defaultValue;
    }

    private static int getInteger(@NonNull String key, int defaultValue) {
        Integer forced = (Integer) FORCED_VALUES.get(key);
        if (forced != null) {
            return forced;
        }

        Object remote = REMOTE_VALUES.get(key);
        if (remote instanceof String) {
            try {
                return Integer.parseInt((String) remote);
            } catch (NumberFormatException e) {
                Log.w(TAG, "Expected an int for key '" + key + "', but got something else! Falling back to the default.");
            }
        }

        return defaultValue;
    }

    /**
     * The default maximum backoff for jobs.
     */
    public static long getDefaultMaxBackoff() {
        return TimeUnit.SECONDS.toMillis(getInteger(DEFAULT_MAX_BACKOFF, 60));
    }

    private static String getString(@NonNull String key, String defaultValue) {
        String forced = (String) FORCED_VALUES.get(key);
        if (forced != null) {
            return forced;
        }

        Object remote = REMOTE_VALUES.get(key);
        if (remote instanceof String) {
            return (String) remote;
        }

        return defaultValue;
    }

    private static Map<String, Object> parseStoredConfig(String stored) {
        Map<String, Object> parsed = new HashMap<>();

        if (TextUtils.isEmpty(stored)) {
            Log.i(TAG, "No remote config stored. Skipping.");
            return parsed;
        }

        try {
            JSONObject root = new JSONObject(stored);
            Iterator<String> iter = root.keys();

            while (iter.hasNext()) {
                String key = iter.next();
                parsed.put(key, root.get(key));
            }
        } catch (JSONException e) {
            throw new AssertionError("Failed to parse! Cleared storage.");
        }

        return parsed;
    }

    private static @NonNull
    String mapToJson(@NonNull Map<String, Object> map) {
        try {
            JSONObject json = new JSONObject();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }

            return json.toString();
        } catch (JSONException e) {
            throw new AssertionError(e);
        }
    }

    private enum VersionFlag {
        /**
         * The flag is no set
         */
        OFF,

        /**
         * The flag is set on for a version higher than the current client version
         */
        ON_IN_FUTURE_VERSION,

        /**
         * The flag is set on for this version or earlier
         */
        ON
    }

    enum Change {
        ENABLED, DISABLED, CHANGED, REMOVED
    }

    @VisibleForTesting
    static final class UpdateResult {
        private final Map<String, Object> memory;
        private final Map<String, Object> disk;
        private final Map<String, Change> memoryChanges;

        UpdateResult(@NonNull Map<String, Object> memory, @NonNull Map<String, Object> disk, @NonNull Map<String, Change> memoryChanges) {
            this.memory = memory;
            this.disk = disk;
            this.memoryChanges = memoryChanges;
        }

        public @NonNull
        Map<String, Object> getMemory() {
            return memory;
        }

        public @NonNull
        Map<String, Object> getDisk() {
            return disk;
        }
    }

}
