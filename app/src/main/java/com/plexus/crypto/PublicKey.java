package com.plexus.crypto;

import com.plexus.protocol.InvalidKeyException;
import com.plexus.protocol.ecc.Curve;
import com.plexus.protocol.ecc.ECPublicKey;
import com.plexus.utils.Conversions;
import com.plexus.utils.Hex;
import com.plexus.utils.Util;
import com.plexus.utils.logging.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PublicKey {

    public static final int KEY_SIZE = 3 + ECPublicKey.KEY_SIZE;
    private static final String TAG = PublicKey.class.getSimpleName();
    private final ECPublicKey publicKey;
    private int id;

    public PublicKey(PublicKey publicKey) {
        this.id = publicKey.id;

        // FIXME :: This not strictly an accurate copy constructor.
        this.publicKey = publicKey.publicKey;
    }

    public PublicKey(int id, ECPublicKey publicKey) {
        this.publicKey = publicKey;
        this.id = id;
    }

    public PublicKey(byte[] bytes, int offset) throws InvalidKeyException {
        Log.i(TAG, "PublicKey Length: " + (bytes.length - offset));

        if ((bytes.length - offset) < KEY_SIZE)
            throw new InvalidKeyException("Provided bytes are too short.");

        this.id = Conversions.byteArrayToMedium(bytes, offset);
        this.publicKey = Curve.decodePoint(bytes, offset + 3);
    }

    public PublicKey(byte[] bytes) throws InvalidKeyException {
        this(bytes, 0);
    }

    public int getType() {
        return publicKey.getType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ECPublicKey getKey() {
        return publicKey;
    }

    public String getFingerprint() {
        return Hex.toString(getFingerprintBytes());
    }

    public byte[] getFingerprintBytes() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(serialize());
        } catch (NoSuchAlgorithmException nsae) {
            Log.w(TAG, "LocalKeyPair", nsae);
            throw new IllegalArgumentException("SHA-1 isn't supported!");
        }
    }

    public byte[] serialize() {
        byte[] keyIdBytes = Conversions.mediumToByteArray(id);
        byte[] serializedPoint = publicKey.serialize();

        Log.i(TAG, "Serializing public key point: " + Hex.toString(serializedPoint));

        return Util.combine(keyIdBytes, serializedPoint);
    }
}
