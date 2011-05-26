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
 *   3 of the License, or (at your option) any later version.*
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

package myorg.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class BundleUtil {

    public static String getStringFromResourceBundle(final String bundle, final String key) {
	final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, Language.getLocale());
	return resourceBundle.getString(key);
    }

    public static String getFormattedStringFromResourceBundle(final String bundle, final String key, String... arguments) {
	String resourceString = getStringFromResourceBundle(bundle, key);
	if (resourceString != null && arguments != null) {
	    for (int i = 0; i < arguments.length; i++) {
		final String argument = arguments[i];
		final String relaceValue = argument == null ? "" : argument;
		resourceString = resourceString.replace("{" + i + "}", relaceValue);
	    }
	}
	return resourceString;
    }

    public static MultiLanguageString getMultilanguageString(final String bundle, final String key) {
	final MultiLanguageString multiLanguageString = new MultiLanguageString();
	final Locale locale = Language.getLocale();
	final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, locale);
	final Language language = Language.valueOf(locale.getLanguage());
	if (resourceBundle != null && resourceBundle.containsKey(key)) {
	    final String content = resourceBundle.getString(key);
	    multiLanguageString.setContent(language, content);
	} else {
	    multiLanguageString.setContent(language, "???" + key + "???");
	}
	return multiLanguageString;
    }

    public static String getLocalizedNamedFroClass(Class<?> someClass) {
	ClassNameBundle annotation = someClass.getAnnotation(ClassNameBundle.class);
	if (annotation != null) {
	    String key = annotation.key();
	    return BundleUtil.getFormattedStringFromResourceBundle(annotation.bundle(), !StringUtils.isEmpty(key) ? key
		    : "label." + someClass.getName());
	} else {
	    return someClass.getName();
	}
    }
}
