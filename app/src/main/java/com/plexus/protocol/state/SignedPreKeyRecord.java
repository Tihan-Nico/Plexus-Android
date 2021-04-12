package com.plexus.protocol.state;

import com.google.protobuf.ByteString;
import com.plexus.databaseprotos.StorageProtos;
import com.plexus.protocol.InvalidKeyException;
import com.plexus.protocol.ecc.Curve;
import com.plexus.protocol.ecc.ECKeyPair;
import com.plexus.protocol.ecc.ECPrivateKey;
import com.plexus.protocol.ecc.ECPublicKey;

import java.io.IOException;

public class SignedPreKeyRecord {

    private StorageProtos.SignedPreKeyRecordStructure structure;

    public SignedPreKeyRecord(int id, long timestamp, ECKeyPair keyPair, byte[] signature) {
        this.structure = StorageProtos.SignedPreKeyRecordStructure.newBuilder()
                .setId(id)
                .setPublicKey(ByteString.copyFrom(keyPair.getPublicKey()
                        .serialize()))
                .setPrivateKey(ByteString.copyFrom(keyPair.getPrivateKey()
                        .serialize()))
                .setSignature(ByteString.copyFrom(signature))
                .setTimestamp(timestamp)
                .build();
    }

    public SignedPreKeyRecord(byte[] serialized) throws IOException {
        this.structure = StorageProtos.SignedPreKeyRecordStructure.parseFrom(serialized);
    }

    public int getId() {
        return this.structure.getId();
    }

    public long getTimestamp() {
        return this.structure.getTimestamp();
    }

    public ECKeyPair getKeyPair() {
        try {
            ECPublicKey publicKey = Curve.decodePoint(this.structure.getPublicKey().toByteArray(), 0);
            ECPrivateKey privateKey = Curve.decodePrivatePoint(this.structure.getPrivateKey().toByteArray());

            return new ECKeyPair(publicKey, privateKey);
        } catch (InvalidKeyException e) {
            throw new AssertionError(e);
        }
    }

    public byte[] getSignature() {
        return this.structure.getSignature().toByteArray();
    }

    public byte[] serialize() {
        return this.structure.toByteArray();
    }
}

