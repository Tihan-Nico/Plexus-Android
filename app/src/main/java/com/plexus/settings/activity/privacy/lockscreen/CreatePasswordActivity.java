package com.plexus.settings.activity.privacy.lockscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.components.components.pinview.OtpEditText;


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

public class CreatePasswordActivity extends AppCompatActivity {

    TextView description;
    SharedPreferences sharedPreferences;
    private OtpEditText otpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_password);

        otpEditText = findViewById(R.id.passcode);
        description = findViewById(R.id.description);

        sharedPreferences = getSharedPreferences("passwords", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        otpEditText.setOnCompleteListener(value -> {
            editor.putBoolean("lockscreen_passcode", true);
            editor.putString("password", otpEditText.getText().toString());
            editor.apply();
            startActivity(new Intent(getApplicationContext(), ConfirmPasswordActivity.class));
        });

    }
}
