package com.plexus.crypto;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plexus.utils.JsonUtils;

import java.io.IOException;

public class AttachmentSecret {

    @JsonProperty
    @JsonSerialize(using = ByteArraySerializer.class)
    @JsonDeserialize(using = ByteArrayDeserializer.class)
    private byte[] classicCipherKey;

    @JsonProperty
    @JsonSerialize(using = ByteArraySerializer.class)
    @JsonDeserialize(using = ByteArrayDeserializer.class)
    private byte[] classicMacKey;

    @JsonProperty
    @JsonSerialize(using = ByteArraySerializer.class)
    @JsonDeserialize(using = ByteArrayDeserializer.class)
    private byte[] modernKey;

    public AttachmentSecret(byte[] classicCipherKey, byte[] classicMacKey, byte[] modernKey) {
        this.classicCipherKey = classicCipherKey;
        this.classicMacKey = classicMacKey;
        this.modernKey = modernKey;
    }

    @SuppressWarnings("unused")
    public AttachmentSecret() {

    }

    public static AttachmentSecret fromString(@NonNull String value) {
        try {
            return JsonUtils.fromJson(value, AttachmentSecret.class);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @JsonIgnore
    public byte[] getClassicCipherKey() {
        return classicCipherKey;
    }

    @JsonIgnore
    public void setClassicCipherKey(byte[] classicCipherKey) {
        this.classicCipherKey = classicCipherKey;
    }

    @JsonIgnore
    public byte[] getClassicMacKey() {
        return classicMacKey;
    }

    @JsonIgnore
    public void setClassicMacKey(byte[] classicMacKey) {
        this.classicMacKey = classicMacKey;
    }

    @JsonIgnore
    public byte[] getModernKey() {
        return modernKey;
    }

    public String serialize() {
        try {
            return JsonUtils.toJson(this);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static class ByteArraySerializer extends JsonSerializer<byte[]> {
        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(Base64.encodeToString(value, Base64.NO_WRAP | Base64.NO_PADDING));
        }
    }

    private static class ByteArrayDeserializer extends JsonDeserializer<byte[]> {

        @Override
        public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return Base64.decode(p.getValueAsString(), Base64.NO_WRAP | Base64.NO_PADDING);
        }
    }


}
