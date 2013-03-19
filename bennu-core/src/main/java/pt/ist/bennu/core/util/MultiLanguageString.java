package pt.ist.bennu.core.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.i18n.I18N;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MultiLanguageString implements Serializable, Comparable<MultiLanguageString> {
    private final Map<Locale, String> contents;

    public static final MultiLanguageString EMPTY = new MultiLanguageString();

    public MultiLanguageString() {
        contents = new HashMap<>();
    }

    public MultiLanguageString(final String content) {
        this(I18N.getLocale(), content);
    }

    public MultiLanguageString(Locale locale, final String content) {
        this();
        if (!Strings.isNullOrEmpty(content)) {
            contents.put(locale, content);
        }
    }

    private MultiLanguageString(Map<Locale, String> contents) {
        this.contents = contents;
    }

    /**
     * 
     * @param locale
     *            the locale of the content
     * @param content
     *            the String with the content in the specified locale
     * @return a <b>new</b> {@link MultiLanguageString} with the given content in the given locale added to the already existing
     *         content NOTE: it does not change the content of this instance
     */
    public MultiLanguageString with(Locale locale, final String content) {
        if (Strings.isNullOrEmpty(content)) {
            return this;
        }
        Map<Locale, String> contents = new HashMap<>();
        contents.putAll(this.contents);
        contents.put(locale, content);
        return new MultiLanguageString(contents);
    }

    /**
     * @see MultiLanguageString#with(Locale, String)
     * @param content
     * @return
     */
    public MultiLanguageString withDefault(final String content) {
        return with(I18N.getLocale(), content);
    }

    public MultiLanguageString without(Locale locale) {
        if (locale != null) {
            Map<Locale, String> contents = new HashMap<>();
            contents.putAll(this.contents);
            contents.remove(locale);
            return new MultiLanguageString(contents);
        }
        return this;
    }

    public MultiLanguageString withoutDefault() {
        return without(I18N.getLocale());
    }

    public MultiLanguageString append(MultiLanguageString string) {
        Map<Locale, String> contents = new HashMap<>();
        Set<Locale> allLocales = new HashSet<>();
        allLocales.addAll(string.getAllLocales());
        allLocales.addAll(getAllLocales());
        for (Locale locale : allLocales) {
            contents.put(locale,
                    StringUtils.defaultString(getContent(locale)) + StringUtils.defaultString(string.getContent(locale)));
        }
        return new MultiLanguageString(contents);
    }

    public MultiLanguageString append(String string) {
        Map<Locale, String> contents = new HashMap<>();
        for (Locale locale : getAllLocales()) {
            contents.put(locale, StringUtils.defaultString(getContent(locale)) + StringUtils.defaultString(string));
        }
        return new MultiLanguageString(contents);
    }

    public Collection<String> getAllContents() {
        return Collections.unmodifiableCollection(contents.values());
    }

    public Collection<Locale> getAllLocales() {
        return Collections.unmodifiableCollection(contents.keySet());
    }

    public boolean isLocaleAvailable() {
        return isLocaleAvailable(I18N.getLocale());
    }

    public boolean isLocaleAvailable(Locale locale) {
        return contents.containsKey(locale);
    }

    /**
     * @return true if this multi locale string contains no locales
     */
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public Locale getContentLocale() {
        Locale locale = I18N.getLocale();
        if (isLocaleAvailable(locale)) {
            return locale;
        }
        locale = Locale.getDefault();
        if (isLocaleAvailable(locale)) {
            return locale;
        }
        return contents.isEmpty() ? null : contents.keySet().iterator().next();
    }

    public String getContent() {
        return getContent(getContentLocale());
    }

    public String getContent(Locale locale) {
        return contents.get(locale);
    }

    public JsonElement json() {
        JsonObject json = new JsonObject();
        for (Locale locale : getAllLocales()) {
            json.addProperty(locale.toLanguageTag(), getContent(locale));
        }
        return json;
    }

    public static MultiLanguageString fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return new MultiLanguageString(json.getAsString());
        }
        Map<Locale, String> contents = new HashMap<>();
        for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            contents.put(Locale.forLanguageTag(entry.getKey()), entry.getValue().getAsString());
        }
        return new MultiLanguageString(contents);
    }

    @Override
    public String toString() {
        final String content = getContent();
        return content == null ? StringUtils.EMPTY : content;
    }

    @Override
    public int compareTo(MultiLanguageString other) {
        return getContent().compareTo(other.getContent());
    }

    public boolean equalInAnyLocale(Object obj) {
        if (obj instanceof MultiLanguageString) {
            MultiLanguageString mls = (MultiLanguageString) obj;
            Set<Locale> locales = new HashSet<>();
            locales.addAll(this.getAllLocales());
            locales.addAll(mls.getAllLocales());
            for (Locale locale : locales) {
                if (this.getContent(locale) != null && this.getContent(locale).equalsIgnoreCase(mls.getContent(locale))) {
                    return true;
                }
            }
        } else if (obj instanceof String) {
            for (final String string : getAllContents()) {
                if (string.equals(obj)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MultiLanguageString) {
            MultiLanguageString mls = (MultiLanguageString) obj;
            if (this.getAllContents().size() != mls.getAllContents().size()) {
                return false;
            }
            for (Locale locale : this.getAllLocales()) {
                if (!getContent(locale).equalsIgnoreCase(mls.getContent(locale))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int sum = 0;
        for (String content : getAllContents()) {
            sum += content.hashCode();
        }
        return sum;
    }
}
