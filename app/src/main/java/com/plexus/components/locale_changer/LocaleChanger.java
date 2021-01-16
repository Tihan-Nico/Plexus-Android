package com.plexus.components.locale_changer;

import android.content.Context;

import com.plexus.components.locale_changer.matcher.LanguageMatchingAlgorithm;
import com.plexus.components.locale_changer.matcher.MatchingAlgorithm;
import com.plexus.components.locale_changer.utils.SystemLocaleRetriever;

import java.util.List;
import java.util.Locale;

public class LocaleChanger {

    private static LocaleChangerDelegate delegate;

    private LocaleChanger() {
    }

    /**
     * Initialize the LocaleChanger, this method needs to be called before calling any other method.
     * <p>
     * If this method was never invoked before it sets a Locale from the supported list if a language match is found with the system Locales,
     * if no match is found the first Locale in the list will be set.<p>
     * If this method was invoked before it will load a Locale previously set.
     * <p>
     * If this method is invoked when the library is already initialized the new settings will be applied from now on.
     *
     * @param context
     * @param supportedLocales a list of your app supported Locales
     * @throws IllegalStateException if the LocaleChanger is already initialized
     */
    public static void initialize(Context context, List<Locale> supportedLocales) {
        initialize(context,
                supportedLocales,
                new LanguageMatchingAlgorithm(),
                LocalePreference.PreferSupportedLocale);
    }

    /**
     * Initialize the LocaleChanger, this method needs to be called before calling any other method.
     * <p>
     * If this method was never invoked before it sets a Locale resolved using the provided {@link MatchingAlgorithm} and {@link LocalePreference}.<p>
     * If this method was invoked before it will load a Locale previously set.
     * <p>
     * If this method is invoked when the library is already initialized the new settings will be applied from now on.
     *
     * @param context
     * @param supportedLocales  a list of your app supported Locales
     * @param matchingAlgorithm used to find a match between supported and system Locales
     * @param preference        used to indicate what Locale is preferred to load in case of a match
     */
    public static void initialize(Context context,
                                  List<Locale> supportedLocales,
                                  MatchingAlgorithm matchingAlgorithm,
                                  LocalePreference preference) {

        delegate = new LocaleChangerDelegate(
                new LocalePersistor(context),
                new LocaleResolver(supportedLocales,
                        SystemLocaleRetriever.retrieve(),
                        matchingAlgorithm,
                        preference),
                new AppLocaleChanger(context)
        );

        delegate.initialize();
    }

    private static void checkInitialization() {
        if (delegate == null)
            throw new IllegalStateException("LocaleChanger is not initialized. Please first call LocaleChanger.initialize");
    }

    /**
     * Clears any Locale set and resolve and load a new default one.
     * This method can be useful if the app implements new supported Locales and it is needed to reload the default one in case there is a best match.
     */
    public static void resetLocale() {
        checkInitialization();
        delegate.resetLocale();
    }

    /**
     * Sets a new default app Locale that will be resolved from the one provided.
     *
     * @param supportedLocale a supported Locale that will be used to resolve the Locale to set.
     */
    public static void setLocale(Locale supportedLocale) {
        checkInitialization();
        delegate.setLocale(supportedLocale);
    }

    /**
     * Gets the supported Locale that has been used to set the app Locale.
     *
     * @return
     */
    public static Locale getLocale() {
        checkInitialization();
        return delegate.getLocale();
    }

    /**
     * This method should be used inside the Activity attachBaseContext.
     * The returned Context should be used as argument for the super method call.
     *
     * @param context
     * @return the resulting context that should be provided to the super method call.
     */
    public static Context configureBaseContext(Context context) {
        checkInitialization();
        return delegate.configureBaseContext(context);
    }

    /**
     * This method should be called from Application#onConfigurationChanged()
     */
    public static void onConfigurationChanged() {
        checkInitialization();
        delegate.onConfigurationChanged();
    }
}
