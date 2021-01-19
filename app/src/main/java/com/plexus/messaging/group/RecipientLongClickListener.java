package com.plexus.messaging.group;

import androidx.annotation.NonNull;

import com.plexus.model.account.User;

public interface RecipientLongClickListener {
    boolean onLongClick(@NonNull User recipient);
}
