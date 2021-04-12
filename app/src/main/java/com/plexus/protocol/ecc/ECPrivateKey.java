package com.plexus.protocol.ecc;

public interface ECPrivateKey {
    public byte[] serialize();

    public int getType();

    byte[] calculateSignature(byte[] publicAddressBytes);
}

