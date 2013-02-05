/* 
 * @(#)BundleUtil.java 
 * 
 * Copyright 2009 Instituto Superior Tecnico 
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
 *  
 *      https://fenix-ashes.ist.utl.pt/ 
 *  
 *   This file is part of the Bennu Web Application Infrastructure. 
 * 
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version  
 *   3 of the License, or (at your option) any later version. 
 * 
 *   Bennu is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *   GNU Lesser General Public License for more details. 
 * 
 *   You should have received a copy of the GNU Lesser General Public License 
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
 *  
 */
package pt.ist.bennu.core.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author Sérgio Silva
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class BundleUtil {
    protected static final Logger LOGGER = Logger.getLogger(BundleUtil.class);

    public static String getStringFromResourceBundle(final String bundle, final String key) throws MissingResourceException {
        return getStringFromResourceBundle(bundle, Language.getLocale(), key);
    }

    public static String getStringFromResourceBundle(final String bundle, Locale locale, final String key) {
        return ResourceBundle.getBundle(bundle, locale).getString(key);
    }

    public static String getFormattedStringFromResourceBundle(final String bundle, final String key, String... args) {
        return getFormattedStringFromResourceBundle(bundle, Language.getLocale(), key, args);
    }

    public static String getFormattedStringFromResourceBundle(final String bundle, Locale locale, final String key,
            String... args) {
        try {
            String message = getStringFromResourceBundle(bundle, locale, key);
            for (int i = 0; i < args.length; i++) {
                message = message.replaceAll("\\{" + i + "\\}", args[i] == null ? "" : args[i]);
            }
            return message;
        } catch (MissingResourceException e) {
            LOGGER.warn(e.getMessage());
            return '!' + key + '!';
        }
    }

    public static MultiLanguageString getMultilanguageString(final String bundle, final String key, String... args) {
        MultiLanguageString mls = new MultiLanguageString();
        for (Language language : VirtualHost.getVirtualHostForThread().getSupportedLanguagesSet()) {
            String message = getFormattedStringFromResourceBundle(bundle, new Locale(language.name()), key, args);
            mls = mls.with(language, message);
        }
        return mls;
    }

    public static String getLocalizedNamedFroClass(Class<?> someClass) {
        ClassNameBundle annotation = someClass.getAnnotation(ClassNameBundle.class);
        if (annotation != null) {
            String key = annotation.key();
            return BundleUtil.getFormattedStringFromResourceBundle(annotation.bundle(),
                    !StringUtils.isEmpty(key) ? key : "label." + someClass.getName());
        }
        return someClass.getName();
    }
}
