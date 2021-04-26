package com.plexus.model.account;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.plexus.core.utils.ThreadUtil;
import com.plexus.core.utils.logging.Log;
import com.plexus.database.DatabaseFactory;
import com.plexus.database.UserDatabase;
import com.plexus.utils.livedata.LiveDataUtil;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class LiveUser {

    private static final String TAG = Log.tag(LiveUser.class);

    private final Context context;
    private final MutableLiveData<User>    liveData;
    private final LiveData<User>           observableLiveData;
    private final LiveData<User> observableLiveDataResolved;
    private final Set<UserForeverObserver> observers;
    private final Observer<User> foreverObserver;
    private final AtomicReference<User>    recipient;
    private final UserDatabase recipientDatabase;
    private final MutableLiveData<Object> refreshForceNotify;

    LiveUser(@NonNull Context context, @NonNull User defaultRecipient) {
        this.context           = context.getApplicationContext();
        this.liveData          = new MutableLiveData<>(defaultRecipient);
        this.recipient         = new AtomicReference<>(defaultRecipient);
        this.recipientDatabase = DatabaseFactory.getRecipientDatabase(context);
        this.observers         = new CopyOnWriteArraySet<>();
        this.foreverObserver   = recipient -> {
            for (UserForeverObserver o : observers) {
                o.onUserChanged(recipient);
            }
        };
        this.refreshForceNotify = new MutableLiveData<>(new Object());
        this.observableLiveData = LiveDataUtil.combineLatest(LiveDataUtil.distinctUntilChanged(liveData, User::hasSameContent),
                refreshForceNotify,
                (recipient, force) -> recipient);
        this.observableLiveDataResolved = LiveDataUtil.filter(this.observableLiveData, r -> !r.isResolving());
    }

    public @NonNull UserId getId() {
        return recipient.get().getId();
    }

    /**
     * @return A recipient that may or may not be fully-resolved.
     */
    public @NonNull User get() {
        return recipient.get();
    }

    /**
     * Watch the recipient for changes. The callback will only be invoked if the provided lifecycle is
     * in a valid state. No need to remove the observer. If you do wish to remove the observer (if,
     * for instance, you wish to remove the listener before the end of the owner's lifecycle), you can
     * use {@link #removeObservers(LifecycleOwner)}.
     */
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<User> observer) {
        ThreadUtil.postToMain(() -> observableLiveData.observe(owner, observer));
    }

    /**
     * Removes all observers of this data registered for the given LifecycleOwner.
     */
    public void removeObservers(@NonNull LifecycleOwner owner) {
        ThreadUtil.runOnMain(() -> observableLiveData.removeObservers(owner));
    }

    /**
     * Watch the recipient for changes. The callback could be invoked at any time. You MUST call
     * {@link #removeForeverObserver(UserForeverObserver)} when finished. You should use
     * {@link #observe(LifecycleOwner, Observer<User>)} if possible, as it is lifecycle-safe.
     */
    public void observeForever(@NonNull UserForeverObserver observer) {
        ThreadUtil.postToMain(() -> {
            if (observers.isEmpty()) {
                observableLiveData.observeForever(foreverObserver);
            }
            observers.add(observer);
        });
    }

    /**
     * Unsubscribes the provided {@link UserForeverObserver} from future changes.
     */
    public void removeForeverObserver(@NonNull UserForeverObserver observer) {
        ThreadUtil.postToMain(() -> {
            observers.remove(observer);

            if (observers.isEmpty()) {
                observableLiveData.removeObserver(foreverObserver);
            }
        });
    }

    /**
     * @return A fully-resolved version of the recipient. May require reading from disk.
     */
    @WorkerThread
    public @NonNull User resolve() {
        User current = recipient.get();

        if (!current.isResolving() || current.getId() == null) {
            return current;
        }

        if (ThreadUtil.isMainThread()) {
            Log.w(TAG, "[Resolve][MAIN] " + getId(), new Throwable());
        }

        User       updated      = fetchAndCacheRecipientFromDisk(getId());
        List<User> participants = Stream.of(updated.getParticipants())
                .filter(User::isResolving)
                .map(User::getId)
                .map(this::fetchAndCacheRecipientFromDisk)
                .toList();

        for (User participant : participants) {
            participant.live().set(participant);
        }

        set(updated);

        return updated;
    }

    @WorkerThread
    public void refresh() {
        refresh(getId());
    }

    /**
     * Forces a reload of the underlying recipient.
     */
    @WorkerThread
    public void refresh(@NonNull UserId id) {
        if (!getId().equals(id)) {
            Log.w(TAG, "Switching ID from " + getId() + " to " + id);
        }

        if (getId().isUnknown()) return;

        if (ThreadUtil.isMainThread()) {
            Log.w(TAG, "[Refresh][MAIN] " + id, new Throwable());
        }

        User       recipient    = fetchAndCacheRecipientFromDisk(id);
        List<User> participants = Stream.of(recipient.getParticipants())
                .map(User::getId)
                .map(this::fetchAndCacheRecipientFromDisk)
                .toList();

        for (User participant : participants) {
            participant.live().set(participant);
        }

        set(recipient);
        refreshForceNotify.postValue(new Object());
    }

    public @NonNull LiveData<User> getLiveData() {
        return observableLiveData;
    }

    public @NonNull LiveData<User> getLiveDataResolved() {
        return observableLiveDataResolved;
    }

    private @NonNull User fetchAndCacheRecipientFromDisk(@NonNull UserId id) {
        RecipientSettings settings = recipientDatabase.getRecipientSettings(id);
        RecipientDetails  details  = settings.getGroupId() != null ? getGroupRecipientDetails(settings)
                : RecipientDetails.forIndividual(context, settings);

        User recipient = new User(id, details, true);
        UserIdCache.INSTANCE.put(recipient);
        return recipient;
    }

    synchronized void set(@NonNull User recipient) {
        this.recipient.set(recipient);
        this.liveData.postValue(recipient);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveUser that = (LiveUser) o;
        return recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipient);
    }

}
