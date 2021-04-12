package com.plexus;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.plexus.utils.DynamicLanguage;
import com.plexus.utils.DynamicTheme;
import com.plexus.utils.PlayStoreUtil;

/**
 * Shown when a users build fully expires.
 */
public class DeprecatedClientActivity extends TracerActivity {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_deprecated_activity);

        findViewById(R.id.client_deprecated_update_button).setOnClickListener(v -> onUpdateClicked());
        findViewById(R.id.client_deprecated_dont_update_button).setOnClickListener(v -> onDontUpdateClicked());
    }

    @Override
    public void onBackPressed() {
        // Disabled
    }

    private void onUpdateClicked() {
        PlayStoreUtil.openPlayStore(this);
    }

    private void onDontUpdateClicked() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.ClientDeprecatedActivity_warning)
                .setMessage(R.string.ClientDeprecatedActivity_your_version_of_plexus_has_expired)
                .setPositiveButton(R.string.ClientDeprecatedActivity_dont_update, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}
