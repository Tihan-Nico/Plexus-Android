package com.plexus.glide.apng.decode;

import com.plexus.glide.apng.io.APNGReader;

import java.io.IOException;

class IHDRChunk extends Chunk {
    static final int ID = fourCCToInt("IHDR");
    /**
     * 图像宽度，以像素为单位
     */
    int width;
    /**
     * 图像高度，以像素为单位
     */
    int height;

    byte[] data = new byte[5];

    @Override
    void innerParse(APNGReader reader) throws IOException {
        width = reader.readInt();
        height = reader.readInt();
        reader.read(data, 0, data.length);
    }
}
