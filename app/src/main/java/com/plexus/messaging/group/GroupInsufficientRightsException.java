package com.plexus.messaging.group;

public final class GroupInsufficientRightsException extends GroupChangeException {

    GroupInsufficientRightsException(Throwable throwable) {
        super(throwable);
    }
}
