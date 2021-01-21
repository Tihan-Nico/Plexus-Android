package com.plexus.settings.activity.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.plexus.R;
import com.plexus.utils.PlexusPreferences;

public class NotificationsActivity extends AppCompatActivity {

    View toolbar;
    SwitchMaterial lookout_notifications;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_notifications);

        toolbar = findViewById(R.id.toolbar);
        lookout_notifications = findViewById(R.id.lookout_notifications);

        init();

    }

    private void init(){

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("Notifications");

        lookout_notifications.setChecked(PlexusPreferences.getLookoutNotificationEnabled(getApplicationContext()));

        lookout_notifications.setOnCheckedChangeListener((buttonView, isChecked) -> PlexusPreferences.setLookoutNotificationEnabled(getApplicationContext(), isChecked));


    }

}
