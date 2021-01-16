package com.plexus.components.locale_changer.matcher;

import com.plexus.components.locale_changer.utils.LocaleMatcher;

import java.util.List;
import java.util.Locale;

/**
 * An algorithm that matches the Locales with most attributes in common.
 */
public final class ClosestMatchingAlgorithm implements MatchingAlgorithm {

    @Override
    public MatchingLocales findDefaultMatch(List<Locale> supportedLocales, List<Locale> systemLocales) {
        MatchingLocales bestMatchingLocalePair = null;
        MatchingLocales languageAndCountryMatchingLocalePair = null;
        MatchingLocales languageMatchingLocalePair = null;

        for (Locale systemLocale : systemLocales) {
            for (Locale supportedLocale : supportedLocales) {

                LocaleMatcher.MatchLevel match = LocaleMatcher.match(systemLocale, supportedLocale);

                if (match.equals(LocaleMatcher.MatchLevel.CompleteMatch)) {
                    bestMatchingLocalePair = new MatchingLocales(supportedLocale, systemLocale);
                    break;

                } else if (match.equals(LocaleMatcher.MatchLevel.LanguageAndCountryMatch)
                        && languageAndCountryMatchingLocalePair == null) {
                    languageAndCountryMatchingLocalePair = new MatchingLocales(supportedLocale, systemLocale);

                } else if (match.equals(LocaleMatcher.MatchLevel.LanguageMatch)
                        && languageMatchingLocalePair == null) {
                    languageMatchingLocalePair = new MatchingLocales(supportedLocale, systemLocale);
                }
            }
            if (bestMatchingLocalePair != null)
                break;
        }

        if (bestMatchingLocalePair != null) {
            return bestMatchingLocalePair;
        } else if (languageAndCountryMatchingLocalePair != null) {
            return languageAndCountryMatchingLocalePair;
        } else {
            return languageMatchingLocalePair;
        }
    }

    @Override
    public MatchingLocales findMatch(Locale supportedLocale, List<Locale> systemLocales) {
        MatchingLocales bestMatchingLocalePair = null;
        MatchingLocales languageAndCountryMatchingLocalePair = null;
        MatchingLocales languageMatchingLocalePair = null;

        for (Locale systemLocale : systemLocales) {
            LocaleMatcher.MatchLevel match = LocaleMatcher.match(systemLocale, supportedLocale);

            if (match.equals(LocaleMatcher.MatchLevel.CompleteMatch)) {
                bestMatchingLocalePair = new MatchingLocales(supportedLocale, systemLocale);
                break;

            } else if (match.equals(LocaleMatcher.MatchLevel.LanguageAndCountryMatch)
                    && languageAndCountryMatchingLocalePair == null) {
                languageAndCountryMatchingLocalePair = new MatchingLocales(supportedLocale, systemLocale);

            } else if (match.equals(LocaleMatcher.MatchLevel.LanguageMatch)
                    && languageMatchingLocalePair == null) {
                languageMatchingLocalePair = new MatchingLocales(supportedLocale, systemLocale);
            }
        }

        if (bestMatchingLocalePair != null) {
            return bestMatchingLocalePair;
        } else if (languageAndCountryMatchingLocalePair != null) {
            return languageAndCountryMatchingLocalePair;
        } else {
            return languageMatchingLocalePair;
        }
    }
}