package pt.ist.bennu.core.util;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class LocaleArray implements Serializable {
    private final List<Locale> locales;

    public LocaleArray(List<Locale> locales) {
        this.locales = locales;
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public LocaleArray with(Locale locale) {
        List<Locale> newlist = new Vector<>(locales);
        newlist.add(locale);
        return new LocaleArray(newlist);
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
        List<Locale> newlist = new Vector<>();
        for (String tag : externalized.split(",")) {
            newlist.add(Locale.forLanguageTag(tag));
        }
        return new LocaleArray(newlist);
    }
}
