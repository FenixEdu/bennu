/*
 * @(#)BundleUtil.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.util;

import java.util.Locale;
import java.util.ResourceBundle;

import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class BundleUtil {

    public static String getStringFromResourceBundle(final String bundle, final String key) {
	final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, Language.getLocale());
	return resourceBundle.getString(key);	
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

}
