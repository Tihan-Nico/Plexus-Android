package com.plexus.settings.activity.advance_features;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class DisplayActivity extends AppCompatActivity {

    private LinearLayout screen_orientation_change;
    private TextView screen_orientation;
    private AlertDialog alertDialog;
    AlertDialog.Builder builder;
    String result = "";
    String[] items = {"Portrait", "Landscape"};
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_display);

        screen_orientation_change = findViewById(R.id.screen_orientation_change);
        screen_orientation = findViewById(R.id.screen_orientation);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        builder = new AlertDialog.Builder(DisplayActivity.this);
        builder.setTitle("Screen Rotation");
        builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
            result = items[which];
            screen_orientation.setText(result);
            alertDialog.dismiss();
        });
        alertDialog = builder.create();

        screen_orientation_change.setOnClickListener(v -> alertDialog.show());

    }

}
