package com.plexus.transport;

import java.io.IOException;

public class PushNetworkException extends IOException {

    public PushNetworkException(Exception exception) {
        super(exception);
    }

    public PushNetworkException(String s) {
        super(s);
    }

}
