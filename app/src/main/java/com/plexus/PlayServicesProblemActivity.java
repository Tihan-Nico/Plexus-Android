package com.plexus;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public class PlayServicesProblemActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PlayServicesProblemFragment fragment = new PlayServicesProblemFragment();
        fragment.show(getSupportFragmentManager(), "dialog");
    }
}
