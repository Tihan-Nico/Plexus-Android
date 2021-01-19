package com.plexus.messaging.group;

import androidx.annotation.NonNull;

import com.plexus.model.account.User;

public interface RecipientClickListener {
    void onClick(@NonNull User recipient);
}
