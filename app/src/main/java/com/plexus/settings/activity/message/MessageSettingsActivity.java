package com.plexus.settings.activity.message;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

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

public class MessageSettingsActivity extends AppCompatActivity {

    private ImageView back;
    private LinearLayout voice_note_filter, sort_by_selection;
    private TextView voice_note_filter_selection, sort_by_selection_text;
    private Switch swipe_to_reply, sounds, enter_to_send;
    private Dialog sort_chat_by, voice_filter;
    private SharedPreferences sharedPreferences;
    private String voice_filter_selected;
    private boolean enabled_swipe_to_reply, enabled_sounds, enabled_enter_to_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_message);

        sharedPreferences = getSharedPreferences("plexus", MODE_PRIVATE);
        voice_filter_selected = sharedPreferences.getString("voice_filter_type", "Default");
        enabled_swipe_to_reply = sharedPreferences.getBoolean("swipe_to_reply", true);
        enabled_sounds = sharedPreferences.getBoolean("sounds", true);
        enabled_enter_to_send = sharedPreferences.getBoolean("enabled_enter_to_send", false);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        back = findViewById(R.id.back);
        voice_note_filter = findViewById(R.id.voice_note_filter);
        sort_by_selection = findViewById(R.id.sort_by_selection);
        voice_note_filter_selection = findViewById(R.id.voice_note_filter_selection);
        sort_by_selection_text = findViewById(R.id.sort_by_selection_text);
        swipe_to_reply = findViewById(R.id.enable_swipe_to_reply);
        sounds = findViewById(R.id.enable_sounds);
        enter_to_send = findViewById(R.id.enable_enter_to_send);

        back.setOnClickListener(v -> finish());

        voice_filter = new Dialog(MessageSettingsActivity.this);
        voice_filter.setContentView(R.layout.dialog_voice_selection);
        voice_filter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        voice_filter.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final ListView voice_list = voice_filter.findViewById(R.id.list);
        voice_list.setOnItemClickListener(
                (adapterView, view, position, id) -> {
                    String selectedFromList = (String) voice_list.getItemAtPosition(position);
                    voice_note_filter_selection.setText(selectedFromList);
                    editor.putString("voice_filter_type", selectedFromList);
                    editor.apply();
                    voice_filter.dismiss();
                });

        voice_note_filter.setOnClickListener(v -> voice_filter.show());

        voice_note_filter_selection.setText(voice_filter_selected);

        if (enabled_swipe_to_reply){
            swipe_to_reply.setChecked(true);
        } else {
            swipe_to_reply.setChecked(false);
        }

        swipe_to_reply.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("swipe_to_reply", true);
            } else {
                editor.putBoolean("swipe_to_reply", false);
            }
            editor.apply();
        });

        if(enabled_sounds){
            sounds.setChecked(true);
        } else {
            sounds.setChecked(false);
        }

        sounds.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("sounds", true);
            } else {
                editor.putBoolean("sounds", false);
            }
            editor.apply();
        });

        if(enabled_enter_to_send){
            enter_to_send.setChecked(true);
        } else {
            enter_to_send.setChecked(false);
        }

        enter_to_send.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor.putBoolean("enabled_enter_to_send", true);
            } else {
                editor.putBoolean("enabled_enter_to_send", false);
            }
            editor.apply();
        });
    }
}
