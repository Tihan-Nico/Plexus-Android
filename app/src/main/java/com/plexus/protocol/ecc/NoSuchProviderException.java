package com.plexus.protocol.ecc;

public class NoSuchProviderException extends RuntimeException {

    private final Throwable nested;

    public NoSuchProviderException(Throwable e) {
        this.nested = e;
    }

    public NoSuchProviderException(String type) {
        super(type);
        this.nested = null;
    }

    public Throwable getNested() {
        return nested;
    }
}
