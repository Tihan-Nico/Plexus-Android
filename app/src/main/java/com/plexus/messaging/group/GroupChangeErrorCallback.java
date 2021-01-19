package com.plexus.messaging.group;

import androidx.annotation.NonNull;

public interface GroupChangeErrorCallback {
    void onError(@NonNull GroupChangeFailureReason failureReason);
}