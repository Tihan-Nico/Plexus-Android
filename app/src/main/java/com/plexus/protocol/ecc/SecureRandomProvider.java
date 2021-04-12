package com.plexus.protocol.ecc;

public interface SecureRandomProvider {
    public void nextBytes(byte[] output);

    public int nextInt(int maxValue);
}
