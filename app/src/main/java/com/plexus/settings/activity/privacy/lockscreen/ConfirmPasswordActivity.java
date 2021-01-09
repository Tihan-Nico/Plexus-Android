package com.plexus.settings.activity.privacy.lockscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.core.components.pinview.OtpEditText;

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

public class ConfirmPasswordActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private OtpEditText otpEditText;
    private String password;
    private CreatePasswordActivity createPasswordActivity;
    private TextView textView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_password_confirm);

        otpEditText = findViewById(R.id.passcode);

        sharedPreferences = getSharedPreferences("passwords", MODE_PRIVATE);
        password = sharedPreferences.getString("password", "");

        otpEditText.setOnCompleteListener(value -> {
            String otp = otpEditText.getText().toString();
            if (otp.equals(password)) {
                startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
            } else {
                Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
                otpEditText.getText().clear();
                startActivity(new Intent(getApplicationContext(), CreatePasswordActivity.class));
                TextView description = ((Activity) context).findViewById(R.id.description);
                description.setText("Please start over");
            }
        });

    }

}
