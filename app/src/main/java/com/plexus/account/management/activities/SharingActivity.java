package com.plexus.account.management.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plexus.R;

public class SharingActivity extends AppCompatActivity {

    RelativeLayout fb_relative, twitter_relative;
    LinearLayout facebook, twitter;
    ImageView fb_logo, twitter_logo, fb_connected, twitter_connected;
    TextView fb_account_name, twitter_account_name, twitter_name, facebook_name;
    View toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_sharing);

        toolbar = findViewById(R.id.toolbar);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        fb_relative = findViewById(R.id.fb_relative);
        twitter_relative = findViewById(R.id.twitter_relative);
        fb_logo = findViewById(R.id.fb_logo);
        twitter_logo = findViewById(R.id.twitter_logo);
        fb_connected = findViewById(R.id.fb_connected);
        twitter_connected = findViewById(R.id.twitter_connected);
        fb_account_name = findViewById(R.id.fb_account_name);
        twitter_account_name = findViewById(R.id.twitter_account_name);
        twitter_name = findViewById(R.id.twitter_name);
        facebook_name = findViewById(R.id.facebook_name);

        init();

    }

    private void init() {

        ImageView back = toolbar.findViewById(R.id.back);
        TextView toolbar_name = toolbar.findViewById(R.id.toolbar_name);

        back.setOnClickListener(v -> finish());

        toolbar_name.setText("Sharing to Other Apps");

    }
}
