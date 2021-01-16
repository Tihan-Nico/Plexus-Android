package com.plexus.components.locale_changer;

import com.plexus.components.locale_changer.matcher.MatchingAlgorithm;
import com.plexus.components.locale_changer.matcher.MatchingLocales;

import java.util.List;
import java.util.Locale;

/**
 * Class that uses a {@link MatchingAlgorithm} and a {@link LocalePreference} to resolve a Locale to be set.
 */
class LocaleResolver {

    private List<Locale> supportedLocales;
    private List<Locale> systemLocales;
    private MatchingAlgorithm matchingAlgorithm;
    private LocalePreference preference;

    LocaleResolver(List<Locale> supportedLocales,
                   List<Locale> systemLocales,
                   MatchingAlgorithm matchingAlgorithm,
                   LocalePreference preference) {

        this.supportedLocales = supportedLocales;
        this.systemLocales = systemLocales;
        this.matchingAlgorithm = matchingAlgorithm;
        this.preference = preference;
    }

    DefaultResolvedLocalePair resolveDefault() {

        MatchingLocales matchingPair = matchingAlgorithm.findDefaultMatch(supportedLocales, systemLocales);

        return matchingPair != null ?
                new DefaultResolvedLocalePair(matchingPair.getSupportedLocale(), matchingPair.getPreferredLocale(preference)) :
                new DefaultResolvedLocalePair(supportedLocales.get(0), supportedLocales.get(0));
    }

    Locale resolve(Locale supportedLocale) throws UnsupportedLocaleException {
        if (!supportedLocales.contains(supportedLocale))
            throw new UnsupportedLocaleException("The Locale you are trying to load is not in the supported list provided on library initialization");

        MatchingLocales matchingPair = null;
        if (preference.equals(LocalePreference.PreferSystemLocale)) {
            matchingPair = matchingAlgorithm.findMatch(supportedLocale, systemLocales);
        }

        return matchingPair != null ?
                matchingPair.getPreferredLocale(preference) :
                supportedLocale;
    }
}