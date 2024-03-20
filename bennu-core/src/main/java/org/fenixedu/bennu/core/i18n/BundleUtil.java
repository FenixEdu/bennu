/*
 * BundleUtil.java
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
package org.fenixedu.bennu.core.i18n;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.i18n.LocalizedString.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

public class BundleUtil {
    private static final Logger logger = LoggerFactory.getLogger(BundleUtil.class);

    public static String getString(final String bundle, final String key, String... args) {
        return getString(bundle, I18N.getLocale(), key, args);
    }

    public static String getString(final String bundle, Locale locale, final String key, String... args) {
        try {
            String message = ResourceBundle.getBundle(bundle, locale).getString(key);
            for (int i = 0; i < args.length; i++) {
                message = message.replaceAll("\\{" + i + "\\}", args[i] == null ? "" : Matcher.quoteReplacement(args[i]));
            }
            return message;
        } catch (MissingResourceException e) {
            logger.debug("Can't find resource for bundle '{}', key '{}' ({})", bundle, key, locale);
            return '!' + key + '!';
        }
    }

    public static LocalizedString getLocalizedString(final String bundle, final String key, String... args) {
        LocalizedString i18NString = new LocalizedString();
        for (Locale locale : CoreConfiguration.supportedLocales()) {
            String message = getString(bundle, locale, key, args);
            i18NString = i18NString.with(locale, message);
        }
        return i18NString;
    }

    // Copied from
    // https://bitbucket.org/qub-it/fenixedu-ulisboa-quality-assurance/src/4fe2a8e9382269acce3a713982c32d26d69adb79/src/main/java/com/qubit/solution/fenixedu/module/ulisboa/qualityassurance/dto/LocalizedStringUtils.java?at=master#LocalizedStringUtils.java-15
    public static com.qubit.terra.framework.tools.primitives.LocalizedString convertToPlatformLocalizedString(
            final org.fenixedu.commons.i18n.LocalizedString localizedString) {
        com.qubit.terra.framework.tools.primitives.LocalizedString result =
                new com.qubit.terra.framework.tools.primitives.LocalizedString();
        for (Locale locale : localizedString.getLocales()) {
            result.setValue(locale, removeTags(localizedString.getOrDefault(locale, "")));
        }
        return result;
    }

    public static org.fenixedu.commons.i18n.LocalizedString convertToBennuLocalizedString(
            final com.qubit.terra.framework.tools.primitives.LocalizedString localizedString) {
        Map<Locale, String> values = localizedString.getValues();
        Builder builder = new org.fenixedu.commons.i18n.LocalizedString.Builder();
        for (Entry<Locale, String> entry : values.entrySet()) {
            builder.with(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public static String removeTags(String text) {
        text = text.replaceAll("\\<.*?\\>", "");
        return text;
    }

}
