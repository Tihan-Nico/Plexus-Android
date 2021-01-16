package com.plexus.video.videoconverter.muxer;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

final class H264Utils {

    private H264Utils() {}

    static @NonNull List<ByteBuffer> getNals(ByteBuffer buffer) {
        final List<ByteBuffer> nals = new ArrayList<>();
        ByteBuffer             nal;
        while ((nal = nextNALUnit(buffer)) != null) {
            nals.add(nal);
        }
        return nals;
    }

    static ByteBuffer nextNALUnit(ByteBuffer buf) {
        skipToNALUnit(buf);
        return gotoNALUnit(buf);
    }

    static void skipToNALUnit(ByteBuffer buf) {
        if (!buf.hasRemaining())
            return;

        int val = 0xffffffff;
        while (buf.hasRemaining()) {
            val <<= 8;
            val |= (buf.get() & 0xff);
            if ((val & 0xffffff) == 1) {
                buf.position(buf.position());
                break;
            }
        }
    }

    /**
     * Finds next Nth H.264 bitstream NAL unit (0x00000001) and returns the data
     * that preceeds it as a ByteBuffer slice
     * <p>
     * Segment byte order is always little endian
     * <p>
     * TODO: emulation prevention
     */
    static ByteBuffer gotoNALUnit(ByteBuffer buf) {

        if (!buf.hasRemaining())
            return null;

        int        from   = buf.position();
        ByteBuffer result = buf.slice();
        result.order(ByteOrder.BIG_ENDIAN);

        int val = 0xffffffff;
        while (buf.hasRemaining()) {
            val <<= 8;
            val |= (buf.get() & 0xff);
            if ((val & 0xffffff) == 1) {
                buf.position(buf.position() - (val == 1 ? 4 : 3));
                result.limit(buf.position() - from);
                break;
            }
        }
        return result;
    }
}

