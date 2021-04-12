package com.plexus.protocol.ecc;

public class DjbECPrivateKey implements ECPrivateKey {

    private final byte[] privateKey;

    DjbECPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public byte[] serialize() {
        return privateKey;
    }

    @Override
    public int getType() {
        return Curve.DJB_TYPE;
    }

    @Override
    public byte[] calculateSignature(byte[] publicAddressBytes) {
        return new byte[0];
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }
}

