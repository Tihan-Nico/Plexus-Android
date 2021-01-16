package com.plexus.components.locale_changer.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.LocaleChangerAppCompatDelegate;

import com.plexus.components.locale_changer.utils.ActivityRecreationHelper;

/**
 * Base {@Activity} class to inherit from with all needed configuration.
 */
public class LocaleChangerBaseActivity extends AppCompatActivity {

    LocaleChangerAppCompatDelegate localeChangerAppCompatDelegate;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        if (localeChangerAppCompatDelegate == null) {
            localeChangerAppCompatDelegate = new LocaleChangerAppCompatDelegate(super.getDelegate());
        }

        return localeChangerAppCompatDelegate;
    }


    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }
}
