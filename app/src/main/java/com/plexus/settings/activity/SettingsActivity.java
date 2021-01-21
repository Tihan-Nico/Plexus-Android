package com.plexus.settings.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;
import com.plexus.account.AccountSettingsActivity;
import com.plexus.settings.activity.advance_features.AdvanceFeaturesActivity;
import com.plexus.settings.activity.experimental.ExperimentalActivity;
import com.plexus.settings.activity.message.MessageSettingsActivity;
import com.plexus.settings.activity.notices.NoticesActivity;
import com.plexus.settings.activity.notifications.NotificationsActivity;
import com.plexus.settings.activity.privacy.PrivacyActivity;
import com.plexus.settings.activity.version.VersionActivity;

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

public class SettingsActivity extends AppCompatActivity {

    LinearLayout advance_features, manage_account, notification_settings, experimental, privacy, version, notices, message;
    TextView about, update;
    ImageView update_available, back;
    private SharedPreferences sharedPreferences;
    private View guideline_1, guideline_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        advance_features = findViewById(R.id.advance_features);
        manage_account = findViewById(R.id.manage_account);
        notification_settings = findViewById(R.id.notification_settings);
        experimental = findViewById(R.id.experimental);
        update_available = findViewById(R.id.update_available);
        back = findViewById(R.id.back);
        update = findViewById(R.id.update);
        privacy = findViewById(R.id.privacy);
        notices = findViewById(R.id.notices);
        version = findViewById(R.id.version);
        message = findViewById(R.id.message);
        about = findViewById(R.id.about);
        guideline_1 = findViewById(R.id.guideline_1);
        guideline_2 = findViewById(R.id.guideline_2);

        init();

    }

    private void init(){
        back.setOnClickListener(v -> finish());

        update.setText(R.string.latest_version);

        sharedPreferences = getSharedPreferences("experimental", MODE_PRIVATE);

        advance_features.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdvanceFeaturesActivity.class)));

        experimental.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ExperimentalActivity.class)));

        version.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), VersionActivity.class)));

        privacy.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PrivacyActivity.class)));

        manage_account.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AccountSettingsActivity.class)));

        about.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AboutActivity.class)));

        notices.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NoticesActivity.class)));

        message.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MessageSettingsActivity.class)));

        notification_settings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean("notices_enabled", false)) {
            guideline_1.setVisibility(View.GONE);
            notices.setVisibility(View.GONE);
        } else {
            guideline_1.setVisibility(View.VISIBLE);
            notices.setVisibility(View.VISIBLE);
        }

        if (sharedPreferences.getBoolean("version_enabled", false)) {
            version.setVisibility(View.GONE);
            guideline_2.setVisibility(View.GONE);
        } else {
            version.setVisibility(View.VISIBLE);
            guideline_2.setVisibility(View.VISIBLE);
        }
    }
}
