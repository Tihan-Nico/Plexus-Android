package com.plexus.glide.common.loader;

import com.plexus.glide.common.io.FileReader;
import com.plexus.glide.common.io.Reader;

import java.io.File;
import java.io.IOException;

public class FileLoader implements Loader {

    private final File mFile;
    private Reader mReader;

    public FileLoader(String path) {
        mFile = new File(path);
    }

    @Override
    public synchronized Reader obtain() throws IOException {
        return new FileReader(mFile);
    }
}
