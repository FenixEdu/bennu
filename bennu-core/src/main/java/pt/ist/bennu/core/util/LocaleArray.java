package pt.ist.bennu.core.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class LocaleArray implements Serializable {
    private final Locale[] locales;

    public LocaleArray(Locale... locales) {
        this.locales = locales;
    }

    public Locale[] getLocales() {
        return locales;
    }

    public LocaleArray with(Locale locale) {
        Locale[] locs = new Locale[locales.length + 1];
        locs[locales.length] = locale;
        return new LocaleArray(locs);
    }

    public String externalize() {
        return Joiner.on(',').join(Iterables.transform(Arrays.asList(locales), new Function<Locale, String>() {
            @Override
            public String apply(Locale locale) {
                return locale.toLanguageTag();
            }
        }));
    }

    public static LocaleArray internalize(String externalized) {
        String[] parts = externalized.split(",");
        Locale[] locales = new Locale[parts.length];
        for (int i = 0; i < parts.length; i++) {
            locales[i] = Locale.forLanguageTag(parts[i]);
        }
        return new LocaleArray(locales);
    }
}
