package com.plexus.messaging.group;

import androidx.annotation.NonNull;

import java.io.IOException;

public enum GroupChangeFailureReason {
    NO_RIGHTS,
    NOT_CAPABLE,
    NOT_A_MEMBER,
    BUSY,
    NETWORK,
    OTHER;

    public static @NonNull
    GroupChangeFailureReason fromException(@NonNull Exception e) {
        if (e instanceof IOException)                         return GroupChangeFailureReason.NETWORK;
        if (e instanceof GroupNotAMemberException)            return GroupChangeFailureReason.NOT_A_MEMBER;
        if (e instanceof GroupChangeBusyException)            return GroupChangeFailureReason.BUSY;
        if (e instanceof GroupInsufficientRightsException)    return GroupChangeFailureReason.NO_RIGHTS;
        return GroupChangeFailureReason.OTHER;
    }
}