package com.plexus.messaging.group;

public final class GroupNotAMemberException extends GroupChangeException {

    public GroupNotAMemberException(Throwable throwable) {
        super(throwable);
    }

    GroupNotAMemberException() {
    }
}
