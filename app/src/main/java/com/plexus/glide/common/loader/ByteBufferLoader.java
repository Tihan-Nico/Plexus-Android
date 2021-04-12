package com.plexus.glide.common.loader;

import com.plexus.glide.common.io.ByteBufferReader;
import com.plexus.glide.common.io.Reader;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ByteBufferLoader implements Loader {
    public abstract ByteBuffer getByteBuffer();

    @Override
    public Reader obtain() throws IOException {
        return new ByteBufferReader(getByteBuffer());
    }
}
