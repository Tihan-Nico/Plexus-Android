package com.plexus.providers;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.plexus.crypto.AttachmentSecret;
import com.plexus.crypto.KeyStoreHelper;
import com.plexus.utils.SecurePreferences;

import java.security.SecureRandom;

public class AttachmentSecretProvider {

    private static AttachmentSecretProvider provider;

    public static synchronized AttachmentSecretProvider getInstance(@NonNull Context context) {
        if (provider == null) provider = new AttachmentSecretProvider(context.getApplicationContext());
        return provider;
    }

    private final Context context;

    private AttachmentSecret attachmentSecret;

    private AttachmentSecretProvider(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized AttachmentSecret getOrCreateAttachmentSecret() {
        if (attachmentSecret != null) return attachmentSecret;

        String unencryptedSecret = SecurePreferences.getAttachmentUnencryptedSecret(context);
        String encryptedSecret   = SecurePreferences.getAttachmentEncryptedSecret(context);

        if      (unencryptedSecret != null) attachmentSecret = getUnencryptedAttachmentSecret(context, unencryptedSecret);
        else if (encryptedSecret != null)   attachmentSecret = getEncryptedAttachmentSecret(encryptedSecret);
        else                                attachmentSecret = createAndStoreAttachmentSecret(context);

        return attachmentSecret;
    }

    public synchronized AttachmentSecret setClassicKey(@NonNull Context context, @NonNull byte[] classicCipherKey, @NonNull byte[] classicMacKey) {
        AttachmentSecret currentSecret    = getOrCreateAttachmentSecret();
        currentSecret.setClassicCipherKey(classicCipherKey);
        currentSecret.setClassicMacKey(classicMacKey);

        storeAttachmentSecret(context, attachmentSecret);

        return attachmentSecret;
    }

    private AttachmentSecret getUnencryptedAttachmentSecret(@NonNull Context context, @NonNull String unencryptedSecret)
    {
        AttachmentSecret attachmentSecret = AttachmentSecret.fromString(unencryptedSecret);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return attachmentSecret;
        } else {
            KeyStoreHelper.SealedData encryptedSecret = KeyStoreHelper.seal(attachmentSecret.serialize().getBytes());

            SecurePreferences.setAttachmentEncryptedSecret(context, encryptedSecret.serialize());
            SecurePreferences.setAttachmentUnencryptedSecret(context, null);

            return attachmentSecret;
        }
    }

    private AttachmentSecret getEncryptedAttachmentSecret(@NonNull String serializedEncryptedSecret) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            throw new AssertionError("OS downgrade not supported. KeyStore sealed data exists on platform < M!");
        } else {
            KeyStoreHelper.SealedData encryptedSecret = KeyStoreHelper.SealedData.fromString(serializedEncryptedSecret);
            return AttachmentSecret.fromString(new String(KeyStoreHelper.unseal(encryptedSecret)));
        }
    }

    private AttachmentSecret createAndStoreAttachmentSecret(@NonNull Context context) {
        SecureRandom random = new SecureRandom();
        byte[]       secret = new byte[32];
        random.nextBytes(secret);

        AttachmentSecret attachmentSecret = new AttachmentSecret(null, null, secret);
        storeAttachmentSecret(context, attachmentSecret);

        return attachmentSecret;
    }

    private void storeAttachmentSecret(@NonNull Context context, @NonNull AttachmentSecret attachmentSecret) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyStoreHelper.SealedData encryptedSecret = KeyStoreHelper.seal(attachmentSecret.serialize().getBytes());
            SecurePreferences.setAttachmentEncryptedSecret(context, encryptedSecret.serialize());
        } else {
            SecurePreferences.setAttachmentUnencryptedSecret(context, attachmentSecret.serialize());
        }
    }

}