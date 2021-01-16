package com.plexus.components.locale_changer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Locale;

class AppLocaleChanger {

    private final Context context;

    AppLocaleChanger(Context context) {
        this.context = context;
    }

    void change(Locale newLocale) {
        Locale.setDefault(newLocale);

        if (SupportedSDK.hasNougat())
            updateConfiguration(newLocale);
        else
            updateConfigurationLegacy(newLocale);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateConfiguration(Locale newLocale) {
        context.getResources().getConfiguration().setLocale(newLocale);
    }

    @SuppressWarnings("deprecation")
    private void updateConfigurationLegacy(Locale newLocale) {
        Configuration conf = context.getResources().getConfiguration();
        conf.locale = newLocale;
        context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());
    }

    Context configureBaseContext(Context context, Locale locale) {
        if (SupportedSDK.hasJellyBeanMr1()) {
            return getLocaleConfiguredContext(context, locale);
        } else {
            return context;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Context getLocaleConfiguredContext(Context context, Locale locale) {
        Configuration conf = context.getResources().getConfiguration();
        conf.setLocale(locale);
        return context.createConfigurationContext(conf);
    }

    private static class SupportedSDK {

        static boolean hasNougat() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
        }

        static boolean hasJellyBeanMr1() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

        }
    }
}
