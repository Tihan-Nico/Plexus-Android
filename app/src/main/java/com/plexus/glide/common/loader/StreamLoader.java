package com.plexus.glide.common.loader;

import com.plexus.glide.common.io.Reader;
import com.plexus.glide.common.io.StreamReader;

import java.io.IOException;
import java.io.InputStream;

public abstract class StreamLoader implements Loader {
    protected abstract InputStream getInputStream() throws IOException;


    public final synchronized Reader obtain() throws IOException {
        return new StreamReader(getInputStream());
    }
}
