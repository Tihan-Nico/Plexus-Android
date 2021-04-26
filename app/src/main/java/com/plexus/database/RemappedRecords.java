package com.plexus.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.plexus.core.utils.guava.Optional;
import com.plexus.core.utils.logging.Log;
import com.plexus.model.account.User;
import com.plexus.model.account.UserId;

import java.util.Map;

/**
 * Merging together recipients and threads is messy business. We can easily replace *almost* all of
 * the references, but there are specific places (notably reactions, jobs, etc) that are really
 * expensive to address. For these cases, we keep mappings of old IDs to new ones to use as a
 * fallback.
 *
 * There should be very few of these, so we keep them in a fast, lazily-loaded memory cache.
 *
 * One important thing to note is that this class will often be accesses inside of database
 * transactions. As a result, it cannot attempt to acquire a database lock while holding a
 * separate lock. Instead, we use the database lock itself as a locking mechanism.
 */
class RemappedRecords {

    private static final String TAG = Log.tag(RemappedRecords.class);

    private static final RemappedRecords INSTANCE = new RemappedRecords();

    private Map<UserId, UserId> recipientMap;
    private Map<String, String>               threadMap;

    private RemappedRecords() {}

    static RemappedRecords getInstance() {
        return INSTANCE;
    }

    @NonNull Optional<UserId> getRecipient(@NonNull Context context, @NonNull UserId oldId) {
        ensureRecipientMapIsPopulated(context);
        return Optional.fromNullable(recipientMap.get(oldId));
    }

    @NonNull Optional<String> getThread(@NonNull Context context, String oldId) {
        ensureThreadMapIsPopulated(context);
        return Optional.fromNullable(threadMap.get(oldId));
    }

    /**
     * Can only be called inside of a transaction.
     */
    void addRecipient(@NonNull Context context, @NonNull UserId oldId, @NonNull UserId newId) {
        Log.w(TAG, "[Recipient] Remapping " + oldId + " to " + newId);
        ensureInTransaction(context);
        ensureRecipientMapIsPopulated(context);
        recipientMap.put(oldId, newId);
        DatabaseFactory.getRemappedRecordsDatabase(context).addRecipientMapping(oldId, newId);
    }

    /**
     * Can only be called inside of a transaction.
     */
    void addThread(@NonNull Context context, String oldId, String newId) {
        Log.w(TAG, "[Thread] Remapping " + oldId + " to " + newId);
        ensureInTransaction(context);
        ensureThreadMapIsPopulated(context);
        threadMap.put(oldId, newId);
        DatabaseFactory.getRemappedRecordsDatabase(context).addThreadMapping(oldId, newId);
    }

    private void ensureRecipientMapIsPopulated(@NonNull Context context) {
        if (recipientMap == null) {
            recipientMap = DatabaseFactory.getRemappedRecordsDatabase(context).getAllRecipientMappings();
        }
    }

    private void ensureThreadMapIsPopulated(@NonNull Context context) {
        if (threadMap == null) {
            threadMap = DatabaseFactory.getRemappedRecordsDatabase(context).getAllThreadMappings();
        }
    }

    private void ensureInTransaction(@NonNull Context context) {
        if (!DatabaseFactory.inTransaction(context)) {
            throw new IllegalStateException("Must be in a transaction!");
        }
    }
}