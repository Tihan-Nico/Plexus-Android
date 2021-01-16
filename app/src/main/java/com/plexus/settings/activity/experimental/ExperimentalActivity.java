package com.plexus.settings.activity.experimental;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.plexus.R;


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

public class ExperimentalActivity extends AppCompatActivity {

    ImageView back;
    private SwitchMaterial notices, version;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_experimental);

        notices = findViewById(R.id.notices);
        version = findViewById(R.id.version);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        sharedPreferences = getSharedPreferences("experimental", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        notices.setChecked(sharedPreferences.getBoolean("notices_enabled", false));

        notices.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("notices_enabled", isChecked);
            editor.apply();
        });

        version.setChecked(sharedPreferences.getBoolean("version_enabled", false));

        version.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("version_enabled", isChecked);
            editor.apply();
        });

    }

}
