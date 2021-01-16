package com.plexus.groups.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;

public class GroupSettingsActivity extends AppCompatActivity {

    ImageView back;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        back = findViewById(R.id.back);

        back.setOnClickListener(view -> finish());

    }
}
