package com.plexus.settings.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.plexus.R;
import com.plexus.components.components.pinview.OtpEditText;
import com.plexus.main.activity.MainActivity;

import static android.content.Context.MODE_PRIVATE;

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

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private SharedPreferences sharedPreferences;
    private String password;

    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
    }


    @Override
    public void onAuthenticationFailed() {
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        ((Activity) context).finish();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

        sharedPreferences = context.getSharedPreferences("passwords", MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");
        OtpEditText otpEditText = ((Activity) context).findViewById(R.id.passcode);
        otpEditText.setText(password);

    }
}
