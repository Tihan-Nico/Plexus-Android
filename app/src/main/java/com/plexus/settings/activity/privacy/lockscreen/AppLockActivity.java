package com.plexus.settings.activity.privacy.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.settings.activity.SettingsActivity;


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

public class AppLockActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private RadioButton passcode, off;
    private RadioGroup radio_group;
    private LinearLayout passcode_active;
    private TextView change_passcode;
    private Switch use_fingerprint;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_lockscreen);

        passcode = findViewById(R.id.passcode);
        off = findViewById(R.id.off);
        radio_group = findViewById(R.id.radio_group);
        passcode_active = findViewById(R.id.passcode_active);
        change_passcode = findViewById(R.id.change_passcode);
        use_fingerprint = findViewById(R.id.use_fingerprint);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        sharedPreferences = getSharedPreferences("passwords", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("lockscreen_passcode", false)) {
            off.setChecked(false);
            passcode.setChecked(true);
            passcode_active.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Fingerprint API only available on from Android 6.0 (M)
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (!fingerprintManager.isHardwareDetected()) {
                    // Device doesn't support fingerprint authentication
                    use_fingerprint.setVisibility(View.GONE);
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    // User hasn't enrolled any fingerprints to authenticate with
                    startActivity(new Intent(Settings.ACTION_FINGERPRINT_ENROLL));
                } else {
                    // Everything is ready for fingerprint authentication
                    use_fingerprint.setVisibility(View.VISIBLE);
                }
            }
        } else {
            passcode.setChecked(false);
            off.setChecked(true);
            passcode_active.setVisibility(View.GONE);
            passcode.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreatePasswordActivity.class)));
        }

        if (sharedPreferences.getBoolean("lockscreen_fingerprint", false)) {
            use_fingerprint.setChecked(true);
        } else {
            use_fingerprint.setChecked(false);
        }

        off.setOnClickListener(v -> {
            editor.putBoolean("lockscreen_passcode", false);
            editor.putBoolean("lockscreen_fingerprint", false);
            editor.apply();
        });

        use_fingerprint.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("lockscreen_fingerprint", true);
            } else {
                editor.putBoolean("lockscreen_fingerprint", false);
            }
            editor.apply();
        });

        change_passcode.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreatePasswordActivity.class)));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
}
