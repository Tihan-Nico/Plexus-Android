package com.plexus.argon;

public final class MemoryCost {

    private final int kib;

    public static MemoryCost KiB(int kib) {
        return new MemoryCost(kib);
    }

    public static MemoryCost MiB(int mib) {
        return new MemoryCost(mib * 1024);
    }

    private MemoryCost(int kib) {
        this.kib = kib;
    }

    /** Number of bytes */
    public long toBytes() {
        return kib * 1024L;
    }

    /** Number of Kibibytes */
    public int getKiB() {
        return kib;
    }
}
