package com.plexus.core.utils.logging;

public class GrowingBuffer {

    private byte[] buffer;

    public byte[] get(int minLength) {
        if (buffer == null || buffer.length < minLength) {
            buffer = new byte[minLength];
        }
        return buffer;
    }
}
