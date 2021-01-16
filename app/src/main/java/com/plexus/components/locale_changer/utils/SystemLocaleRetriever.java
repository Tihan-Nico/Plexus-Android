package com.plexus.components.locale_changer.utils;

import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A class useful to retrieve the system configured Locales.
 */
public class SystemLocaleRetriever {

    private SystemLocaleRetriever() {
    }

    public static List<Locale> retrieve() {
        List<Locale> systemLocales;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemLocales = mapToListOfLocales(LocaleList.getDefault());
        } else {
            systemLocales = Collections.singletonList(Locale.getDefault());
        }
        return systemLocales;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static List<Locale> mapToListOfLocales(LocaleList localeList) {
        List<Locale> locales = new ArrayList<>();
        for (int i = 0; i < localeList.size(); i++) {
            locales.add(localeList.get(i));
        }
        return locales;
    }
}
