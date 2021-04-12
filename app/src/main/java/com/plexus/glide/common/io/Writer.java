package com.plexus.glide.common.io;

import java.io.IOException;

public interface Writer {
    void reset(int size);

    void putByte(byte b);

    void putBytes(byte[] b);

    int position();

    void skip(int length);

    byte[] toByteArray();

    void close() throws IOException;
}
