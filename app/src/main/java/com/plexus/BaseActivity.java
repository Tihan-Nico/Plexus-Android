package com.plexus;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.LocaleChangerAppCompatDelegate;

import com.plexus.components.locale_changer.utils.ActivityRecreationHelper;
import com.plexus.core.utils.logging.Log;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.utils.AppStartup;
import com.plexus.utils.ConfigurationUtil;
import com.plexus.utils.PlexusPreferences;
import com.plexus.utils.dynamiclanguage.DynamicLanguageContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = Log.tag(BaseActivity.class);
    private LocaleChangerAppCompatDelegate localeChangerAppCompatDelegate;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        if (localeChangerAppCompatDelegate == null) {
            localeChangerAppCompatDelegate = new LocaleChangerAppCompatDelegate(super.getDelegate());
        }
        return localeChangerAppCompatDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppStartup.getInstance().onCriticalRenderEventStart();
        logEvent("onCreate()");
        super.onCreate(savedInstanceState);
        AppStartup.getInstance().onCriticalRenderEventEnd();

        /*ShortcutUtil.checkIfShortcutExists(this);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);
        initializeScreenshotSecurity();
    }

    @Override
    protected void onStart() {
        logEvent("onStart()");
        PlexusDependencies.getShakeToReport().registerActivity(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        logEvent("onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logEvent("onDestroy()");
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }

    private void initializeScreenshotSecurity() {
        if (PlexusPreferences.getScreenSecurityEnabled(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    private void logEvent(@NonNull String event) {
        Log.d(TAG, "[" + Log.tag(getClass()) + "] " + event);
    }

    @Override
    public void applyOverrideConfiguration(@NonNull Configuration overrideConfiguration) {
        DynamicLanguageContextWrapper.prepareOverrideConfiguration(this, overrideConfiguration);
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    @Override
    protected void attachBaseContext(@NonNull Context newBase) {
        super.attachBaseContext(newBase);

        Configuration configuration = new Configuration(newBase.getResources().getConfiguration());
        int appCompatNightMode = getDelegate().getLocalNightMode() != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED ? getDelegate().getLocalNightMode()
                : AppCompatDelegate.getDefaultNightMode();

        configuration.uiMode = (configuration.uiMode & ~Configuration.UI_MODE_NIGHT_MASK) | mapNightModeToConfigurationUiMode(newBase, appCompatNightMode);

        applyOverrideConfiguration(configuration);
    }

    private static int mapNightModeToConfigurationUiMode(@NonNull Context context, @AppCompatDelegate.NightMode int appCompatNightMode) {
        if (appCompatNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return Configuration.UI_MODE_NIGHT_YES;
        } else if (appCompatNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return Configuration.UI_MODE_NIGHT_NO;
        }
        return ConfigurationUtil.getNightModeConfiguration(context.getApplicationContext());
    }
}
