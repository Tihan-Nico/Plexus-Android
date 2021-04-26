package com.plexus.model.account;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import com.plexus.core.utils.concurrent.PlexusExecutors;
import com.plexus.core.utils.logging.Log;
import com.plexus.database.DatabaseFactory;
import com.plexus.database.UserDatabase;
import com.plexus.utils.LRUCache;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.concurrent.FilteredExecutor;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.plexus.database.UserDatabase.*;

public class LiveUserCache {

    private static final String TAG = Log.tag(LiveUserCache.class);

    private static final int CACHE_MAX      = 1000;
    private static final int CACHE_WARM_MAX = 500;

    private final Context context;
    private final UserDatabase recipientDatabase;
    private final Map<UserId, LiveUser> recipients;
    private final LiveUser                   unknown;
    private final Executor executor;
    private final SQLiteDatabase db;

    private volatile UserId localRecipientId;

    private boolean warmedUp;

    @SuppressLint("UseSparseArrays")
    public LiveUserCache(@NonNull Context context) {
        this.context           = context.getApplicationContext();
        this.recipientDatabase = DatabaseFactory.getRecipientDatabase(context);
        this.recipients        = new LRUCache<>(CACHE_MAX);
        this.unknown           = new LiveUser(context, User.UNKNOWN);
        this.db                = DatabaseFactory.getInstance(context).getRawDatabase();
        this.executor          = new FilteredExecutor(PlexusExecutors.BOUNDED, () -> !db.isDbLockedByCurrentThread());
    }

    @AnyThread
    synchronized @NonNull LiveUser getLive(@NonNull UserId id) {
        if (id.isUnknown()) return unknown;

        LiveUser live = recipients.get(id);

        if (live == null) {
            final LiveUser newLive = new LiveUser(context, new User(id));

            recipients.put(id, newLive);

            MissingRecipientException prettyStackTraceError = new MissingRecipientException(newLive.getId());

            executor.execute(() -> {
                try {
                    newLive.resolve();
                } catch (MissingRecipientException e) {
                    throw prettyStackTraceError;
                }
            });

            live = newLive;
        }

        return live;
    }

    /**
     * Adds a recipient to the cache if we don't have an entry. This will also update a cache entry
     * if the provided recipient is resolved, or if the existing cache entry is unresolved.
     *
     * If the recipient you add is unresolved, this will enqueue a resolve on a background thread.
     */
    @AnyThread
    public synchronized void addToCache(@NonNull Collection<User> newRecipients) {
        for (User recipient : newRecipients) {
            LiveUser live         = recipients.get(recipient.getId());
            boolean       needsResolve = false;

            if (live == null) {
                live = new LiveUser(context, recipient);
                recipients.put(recipient.getId(), live);
                needsResolve = recipient.isResolving();
            } else if (live.get().isResolving() || !recipient.isResolving()) {
                live.set(recipient);
                needsResolve = recipient.isResolving();
            }

            if (needsResolve) {
                MissingRecipientException prettyStackTraceError = new MissingRecipientException(recipient.getId());
                executor.execute(() -> {
                    try {
                        recipient.resolve();
                    } catch (MissingRecipientException e) {
                        throw prettyStackTraceError;
                    }
                });
            }
        }
    }

    @NonNull User getSelf() {
        if (localRecipientId == null) {
            if (localRecipientId == null) {
                throw new MissingRecipientException(null);
            }
        }

        return getLive(localRecipientId).resolve();
    }

    @AnyThread
    public synchronized void warmUp() {
        if (warmedUp) {
            return;
        } else {
            warmedUp = true;
        }
    }

    @AnyThread
    public synchronized void clearSelf() {
        localRecipientId = null;
    }

    @AnyThread
    public synchronized void clear() {
        recipients.clear();
    }

}
