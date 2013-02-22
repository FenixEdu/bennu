/*
 * MultiLanguageString.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MultiLanguageString implements Serializable, Comparable<MultiLanguageString> {
    private final Map<Language, String> contents;

    public MultiLanguageString() {
        contents = new HashMap<>();
    }

    public MultiLanguageString(final String content) {
        this();
        final Language language = Language.getUserLanguage();
        if (language == null) {
            throw new IllegalArgumentException("no user language is set");
        }
        if (content != null) {
            contents.put(language, content);
        }
    }

    public MultiLanguageString(@Nonnull Language language, final String content) {
        this();
        if (language == null) {
            throw new IllegalArgumentException("language cannot be null");
        }
        if (content != null) {
            contents.put(language, content);
        }
    }

    private MultiLanguageString(Map<Language, String> contents) {
        this.contents = contents;
    }

    /**
     * 
     * @param language
     *            the language of the content
     * @param content
     *            the String with the content in the specified language
     * @return a <b>new</b> {@link MultiLanguageString} with the given content in the given language added to the already existing
     *         content NOTE: it does not change the content of this instance
     */
    public MultiLanguageString with(@Nonnull Language language, final String content) {
        if (content == null) {
            return this;
        }
        if (language == null) {
            throw new IllegalArgumentException("language cannot be null");
        }
        Map<Language, String> contents = new HashMap<>();
        contents.putAll(contents);
        contents.put(language, content);
        return new MultiLanguageString(contents);
    }

    /**
     * @see MultiLanguageString#with(Language, String)
     * @param content
     * @return
     */
    public MultiLanguageString withDefault(final String content) {
        final Language userLanguage = Language.getUserLanguage();
        if (userLanguage == null) {
            throw new IllegalArgumentException("no user language is set");
        }
        return with(userLanguage, content);
    }

    public MultiLanguageString without(Language language) {
        if (language != null) {
            Map<Language, String> contents = new HashMap<>();
            contents.putAll(contents);
            contents.remove(language);
            return new MultiLanguageString(contents);
        }
        return this;
    }

    public Collection<String> getAllContents() {
        return contents.values();
    }

    public Collection<Language> getAllLanguages() {
        return contents.keySet();
    }

    public boolean isRequestedLanguage() {
        Language userLanguage = Language.getUserLanguage();
        return userLanguage != null && userLanguage.equals(getContentLanguage());
    }

    public Language getContentLanguage() {
        Language userLanguage = Language.getUserLanguage();
        if (userLanguage != null && hasLanguage(userLanguage)) {
            return userLanguage;
        }

        Language systemLanguage = Language.getDefaultLanguage();
        if (systemLanguage != null && hasLanguage(systemLanguage)) {
            return systemLanguage;
        }

        return contents.isEmpty() ? null : contents.keySet().iterator().next();
    }

    public String getContent() {
        return getContent(getContentLanguage());
    }

    public String getContent(Language language) {
        return contents.get(language);
    }

    public String getPreferedContent() {
        return hasLanguage(Language.getDefaultLanguage()) ? getContent(Language.getDefaultLanguage()) : getContent();
    }

    public String toUpperCase() {
        return hasContent() ? getContent().toUpperCase() : null;
    }

    public boolean hasContent() {
        // return getContent() != null;
        return !isEmpty();
    }

    public boolean hasContent(Language language) {
        return !StringUtils.isEmpty(getContent(language));
    }

    public boolean hasLanguage(Language language) {
        return contents.containsKey(language);
    }

    public String exportAsString() {
        final StringBuilder result = new StringBuilder();
        for (final Entry<Language, String> entry : contents.entrySet()) {
            final Language key = entry.getKey();
            final String value = entry.getValue();
            result.append(key);
            result.append(value.length());
            result.append(':');
            result.append(value);
        }
        return result.toString();
    }

    public MultiLanguageString append(MultiLanguageString string) {
        Map<Language, String> contents = new HashMap<>();
        Set<Language> allLanguages = new HashSet<>();
        allLanguages.addAll(string.getAllLanguages());
        allLanguages.addAll(getAllLanguages());
        for (Language language : allLanguages) {
            contents.put(language,
                    StringUtils.defaultString(getContent(language)) + StringUtils.defaultString(string.getContent(language)));
        }
        return new MultiLanguageString(contents);
    }

    public MultiLanguageString append(String string) {
        Map<Language, String> contents = new HashMap<>();
        for (Language language : getAllLanguages()) {
            contents.put(language, StringUtils.defaultString(getContent(language)) + StringUtils.defaultString(string));
        }
        return new MultiLanguageString(contents);
    }

    /**
     * @return true if this multi language string contains no languages
     */
    public boolean isEmpty() {
        // return this.getAllLanguages().isEmpty();
        return contents.isEmpty();
    }

    public static MultiLanguageString importFromString(String string) {
        if (string == null) {
            return null;
        }

        Map<Language, String> contents = new HashMap<>();
        String nullContent = StringUtils.EMPTY;

        for (int i = 0; i < string.length();) {

            int length = 0;
            int collonPosition = string.indexOf(':', i + 2);

            if (!StringUtils.isNumeric(string.substring(i + 2, collonPosition))) {
                length = Integer.parseInt(string.substring(i + 4, collonPosition));
                nullContent = string.substring(collonPosition + 1, collonPosition + 1 + length);

            } else {
                length = Integer.parseInt(string.substring(i + 2, collonPosition));
                String language = string.substring(i, i + 2);
                String content = string.substring(collonPosition + 1, collonPosition + 1 + length);
                contents.put(Language.valueOf(language), content);
            }

            i = collonPosition + 1 + length;
        }

        // HACK: MultiLanguageString should not allow null values as language
        if (contents.isEmpty()) {
            contents.put(Language.getDefaultLanguage(), nullContent);
        }

        return new MultiLanguageString(contents);
    }

    @Override
    public String toString() {
        final String content = getContent();
        return content == null ? StringUtils.EMPTY : content;
    }

    @Override
    public int compareTo(MultiLanguageString languageString) {
        if (!hasContent() && !languageString.hasContent()) {
            return 0;
        }

        if (!hasContent() && languageString.hasContent()) {
            return -1;
        }

        if (hasContent() && !languageString.hasContent()) {
            return 1;
        }

        return Collator.getInstance().compare(getContent(), languageString.getContent());
    }

    public boolean equalInAnyLanguage(Object obj) {
        if (obj instanceof MultiLanguageString) {
            MultiLanguageString multiLanguageString = (MultiLanguageString) obj;
            Set<Language> languages = new HashSet<>();
            languages.addAll(this.getAllLanguages());
            languages.addAll(multiLanguageString.getAllLanguages());
            for (Language language : languages) {
                if (this.getContent(language) != null
                        && this.getContent(language).equalsIgnoreCase(multiLanguageString.getContent(language))) {
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

    public JsonObject json() {
        JsonObject json = new JsonObject();
        for (Language language : getAllLanguages()) {
            json.addProperty(language.name(), getContent(language));
        }
        return json;
    }

    public static MultiLanguageString fromJson(JsonObject mlsJsonString) {
        Map<Language, String> contents = new HashMap<>();
        for (Entry<String, JsonElement> entry : mlsJsonString.entrySet()) {
            contents.put(Language.valueOf(entry.getKey()), entry.getValue().getAsString());
        }
        return new MultiLanguageString(contents);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MultiLanguageString) {
            MultiLanguageString multiLanguageString = (MultiLanguageString) obj;
            if (this.getAllContents().size() != multiLanguageString.getAllContents().size()) {
                return false;
            }
            for (Language language : this.getAllLanguages()) {
                if (!getContent(language).equalsIgnoreCase(multiLanguageString.getContent(language))) {
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
