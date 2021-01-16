package com.plexus.components.locale_changer;

import android.content.Context;

import java.util.Locale;

/**
 * A delegate instance class that is used by the public LocaleChanger class.
 */
class LocaleChangerDelegate {

    private LocalePersistor persistor;
    private LocaleResolver resolver;
    private AppLocaleChanger appLocaleChanger;

    private Locale currentLocale;

    LocaleChangerDelegate(LocalePersistor persistor,
                          LocaleResolver resolver,
                          AppLocaleChanger appLocaleChanger) {
        this.persistor = persistor;
        this.resolver = resolver;
        this.appLocaleChanger = appLocaleChanger;
    }

    void initialize() {
        Locale persistedLocale = persistor.load();

        if (persistedLocale != null)
            try {
                currentLocale = resolver.resolve(persistedLocale);
            } catch (UnsupportedLocaleException e) {
                persistedLocale = null;
            }

        if (persistedLocale == null) {
            DefaultResolvedLocalePair defaultLocalePair = resolver.resolveDefault();

            currentLocale = defaultLocalePair.getResolvedLocale();

            persistor.save(defaultLocalePair.getSupportedLocale());
        }

        appLocaleChanger.change(currentLocale);
    }

    void resetLocale() {
        persistor.clear();
        initialize();
    }

    void setLocale(Locale supportedLocale) {
        try {
            currentLocale = resolver.resolve(supportedLocale);

            persistor.save(supportedLocale);

            appLocaleChanger.change(currentLocale);

        } catch (UnsupportedLocaleException e) {
            throw new IllegalArgumentException(e);
        }

    }

    Locale getLocale() {
        return persistor.load();
    }

    Context configureBaseContext(Context context) {
        return appLocaleChanger.configureBaseContext(context, currentLocale);
    }

    void onConfigurationChanged() {
        appLocaleChanger.change(currentLocale);
    }
}
