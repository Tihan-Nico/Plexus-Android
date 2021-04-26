package com.plexus.model.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.core.utils.logging.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Thread safe cache that allows faster looking up of {@link UserId}s without hitting the database.
 */
final class UserIdCache {

    private static final int INSTANCE_CACHE_LIMIT = 1000;

    static final UserIdCache INSTANCE = new UserIdCache(INSTANCE_CACHE_LIMIT);

    private static final String TAG = Log.tag(UserIdCache.class);

    private final Map<Object, UserId> ids;

    UserIdCache(int limit) {
        ids = new LinkedHashMap<Object, UserId>(128, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Entry<Object, UserId> eldest) {
                return size() > limit;
            }
        };
    }

    synchronized void put(@NonNull User recipient) {
        UserId      recipientId = UserId.from(recipient.getId());
    }

    synchronized @Nullable UserId get(@Nullable UUID uuid, @Nullable String e164) {
        if (uuid != null && e164 != null) {
            UserId recipientIdByUuid = ids.get(uuid);
            if (recipientIdByUuid == null) return null;

            UserId recipientIdByE164 = ids.get(e164);
            if (recipientIdByE164 == null) return null;

            if (recipientIdByUuid.equals(recipientIdByE164)) {
                return recipientIdByUuid;
            } else {
                ids.remove(uuid);
                ids.remove(e164);
                Log.w(TAG, "Seen invalid UserIdCacheState");
                return null;
            }
        } else if (uuid != null) {
            return ids.get(uuid);
        } else if (e164 != null) {
            return ids.get(e164);
        }

        return null;
    }

    synchronized void clear() {
        ids.clear();
    }
}
