package pt.ist.bennu.core.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class LocaleArray implements Serializable {
    private final Set<Locale> locales;

    public LocaleArray(Set<Locale> locales) {
        this.locales = Collections.unmodifiableSet(locales);
    }

    public Set<Locale> getLocales() {
        return locales;
    }

    public LocaleArray with(Locale locale) {
        Set<Locale> localeSet = new HashSet<>(locales);
        localeSet.add(locale);
        return new LocaleArray(localeSet);
    }

    public String externalize() {
        return Joiner.on(',').join(Iterables.transform(locales, new Function<Locale, String>() {
            @Override
            public String apply(Locale locale) {
                return locale.toLanguageTag();
            }
        }));
    }

    public static LocaleArray internalize(String externalized) {
        Set<Locale> localeSet = new HashSet<>();
        for (String tag : externalized.split(",")) {
            localeSet.add(Locale.forLanguageTag(tag));
        }
        return new LocaleArray(localeSet);
    }
}
