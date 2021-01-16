package com.plexus.components.locale_changer.matcher;

import com.plexus.components.locale_changer.utils.LocaleMatcher;

import java.util.List;
import java.util.Locale;

/**
 * An algorithm that match the Locales the same language in common.
 */
public final class LanguageMatchingAlgorithm implements MatchingAlgorithm {

    @Override
    public MatchingLocales findDefaultMatch(List<Locale> supportedLocales, List<Locale> systemLocales) {
        MatchingLocales matchingPair = null;
        for (Locale systemLocale : systemLocales) {
            Locale matchingSupportedLocale = findMatchingLocale(systemLocale, supportedLocales);
            if (matchingSupportedLocale != null) {
                matchingPair = new MatchingLocales(matchingSupportedLocale, systemLocale);
                break;
            }
        }
        return matchingPair;
    }

    @Override
    public MatchingLocales findMatch(Locale supportedLocale, List<Locale> systemLocales) {
        Locale matchingSystemLocale = findMatchingLocale(supportedLocale, systemLocales);
        return matchingSystemLocale != null ?
                new MatchingLocales(supportedLocale, matchingSystemLocale) :
                null;
    }

    private static Locale findMatchingLocale(Locale localeToMatch, List<Locale> candidates) {
        Locale matchingLocale = null;
        for (Locale candidate : candidates) {
            LocaleMatcher.MatchLevel matchLevel = LocaleMatcher.match(localeToMatch, candidate);

            if (matchLevel != LocaleMatcher.MatchLevel.NoMatch) {
                matchingLocale = candidate;
                break;
            }
        }
        return matchingLocale;
    }
}
