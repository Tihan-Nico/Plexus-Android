package com.plexus.model.account;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public interface UserForeverObserver {
    @MainThread
    void onUserChanged(@NonNull User recipient);
}
