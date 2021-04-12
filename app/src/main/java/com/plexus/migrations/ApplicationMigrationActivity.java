package com.plexus.migrations;

import android.content.Intent;
import android.os.Bundle;

import com.plexus.R;
import com.plexus.TracerActivity;
import com.plexus.main.activity.MainActivity;
import com.plexus.utils.DynamicLanguage;
import com.plexus.utils.DynamicTheme;
import com.plexus.utils.logging.Log;

/**
 * An activity that can be shown to block access to the rest of the app when a long-running or
 * otherwise blocking application-level migration is happening.
 */
public class ApplicationMigrationActivity extends TracerActivity {

    private static final String TAG = Log.tag(ApplicationMigrationActivity.class);

    private final DynamicTheme dynamicTheme    = new DynamicTheme();
    private final DynamicLanguage dynamicLanguage = new DynamicLanguage();

    @Override
    protected void onPreCreate() {
        dynamicTheme.onCreate(this);
        dynamicLanguage.onCreate(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, boolean isReady) {
        super.onCreate(savedInstanceState, true);

        ApplicationMigrations.getUiBlockingMigrationStatus().observe(this, running -> {
            if (running == null) {
                return;
            }

            if (running) {
                Log.i(TAG, "UI-blocking migration is in progress. Showing spinner.");
                setContentView(R.layout.application_migration_activity);
            } else {
                Log.i(TAG, "UI-blocking migration is no-longer in progress. Finishing.");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
