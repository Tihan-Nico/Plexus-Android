package com.plexus.components.locale_changer.matcher;

import java.util.List;
import java.util.Locale;

/**
 * Matching algorithm that is used by the library.
 */
public interface MatchingAlgorithm {

    /**
     * Method that implements the algorithm to find two matching Locales between a list of supported and system Locales.
     *
     * @param supportedLocales a list of your app supported locales
     * @param systemLocales    a list of the configured  locales in system preferences
     * @return a {@link MatchingLocales} containing the pair of matching locales. If no match is found null is returned
     */
    MatchingLocales findDefaultMatch(List<Locale> supportedLocales, List<Locale> systemLocales);

    /**
     * Method that implements the algorithm to find a matching Locale between the supported Locale and system Locales.
     *
     * @param supportedLocale one of your app supported locales
     * @param systemLocales   a list of the configured  locales in system preferences
     * @return a {@link MatchingLocales} containing the pair of matching locales. If no match is found null is returned
     */
    MatchingLocales findMatch(Locale supportedLocale, List<Locale> systemLocales);
}
