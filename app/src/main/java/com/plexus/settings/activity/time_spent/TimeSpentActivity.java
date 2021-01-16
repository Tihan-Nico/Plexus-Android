package com.plexus.settings.activity.time_spent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.plexus.R;
import com.plexus.utils.TimeConverter;

import java.io.IOException;
import java.security.GeneralSecurityException;

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

public class TimeSpentActivity extends AppCompatActivity {

    //These are being used for determining how long the user has been on this Fragments
    SharedPreferences sharedPreferences = null;
    String masterKeyAlias = null;
    long total_time;
    private TextView home_total_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_time_spent);

        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "plexus_time_spent",
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        long start_time = sharedPreferences.getLong("home_start_time", 0);
        long end_time = sharedPreferences.getLong("home_end_time", 0);
        long previous_time = sharedPreferences.getLong("home_previous_time", 0);
        if (previous_time > 0) {
            total_time = previous_time + start_time - end_time;
        } else {
            total_time = start_time - end_time;
        }

        home_total_time = findViewById(R.id.home_total_time);

        home_total_time.setText(TimeConverter.getDurationBreakdown(total_time));

    }
}
