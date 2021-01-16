package com.plexus.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class MasterCipher {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] key = {127, 109, -51, 26, -105, 84, -120, -123, 68, 18, 99, 52, -3, -125, -119, -70};
    private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(key, "AES");
    private static Cipher cipherE, cipherD;


    @SuppressLint("GetInstance")
    public static String encrypt(final String message) {
        try {
            cipherE = Cipher.getInstance(ALGORITHM);
            cipherE.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC);

            byte[] encryptedBytes = cipherE.doFinal(message.getBytes());
            String base64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            return base64;
        } catch (Exception e) {
            e.getMessage();
        }
        return message;
    }

    @SuppressLint("GetInstance")
    public static String decrypt(final String message) {
        try {
            cipherD = Cipher.getInstance(ALGORITHM);
            cipherD.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC);
            byte[] raw = Base64.decode(message, Base64.DEFAULT);

            byte[] decryptedBytes = cipherD.doFinal(raw);
            String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);
            return decryptedMessage;
        } catch (Exception e) {
            e.getMessage();
        }
        return message;
    }
}
