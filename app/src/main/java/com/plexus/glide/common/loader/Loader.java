package com.plexus.glide.common.loader;

import com.plexus.glide.common.io.Reader;

import java.io.IOException;

public interface Loader {
    Reader obtain() throws IOException;
}
