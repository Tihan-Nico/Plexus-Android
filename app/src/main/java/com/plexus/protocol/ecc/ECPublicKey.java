package com.plexus.protocol.ecc;

public interface ECPublicKey extends Comparable<ECPublicKey> {

    public static final int KEY_SIZE = 33;

    public byte[] serialize();

    public int getType();

    boolean verifySignature(byte[] bytes, byte[] signature);
}

