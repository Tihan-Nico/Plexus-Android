package com.plexus.glide.apng.decode;

import com.plexus.glide.apng.io.APNGReader;

import java.io.IOException;

class ACTLChunk extends Chunk {
    static final int ID = fourCCToInt("acTL");
    int num_frames;
    int num_plays;

    @Override
    void innerParse(APNGReader apngReader) throws IOException {
        num_frames = apngReader.readInt();
        num_plays = apngReader.readInt();
    }
}
