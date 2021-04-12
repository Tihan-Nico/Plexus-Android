package com.plexus.glide.apng.decode;


import com.plexus.glide.apng.io.APNGReader;

import java.io.IOException;

class FDATChunk extends Chunk {
    static final int ID = fourCCToInt("fdAT");
    int sequence_number;

    @Override
    void innerParse(APNGReader reader) throws IOException {
        sequence_number = reader.readInt();
    }
}
