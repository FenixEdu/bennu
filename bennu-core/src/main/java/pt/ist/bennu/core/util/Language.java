/*
 * Language.java
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

import java.util.Locale;

public enum Language {

    pt, en, es, de, fr, it, ar, bg, cs, da, el, eo, et, fi, hr, hu, id, is, ja, ko, lt, lv, nl, no, pl, ro, ru, sk, sl, sr, sv,
    th, tr, uk, zh;

    private static Locale defaultLocale = null;

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(final Locale locale) {
        defaultLocale = locale;
    }

    public static Language getDefaultLanguage() {
        final Locale defaultLocale = getDefaultLocale();
        return defaultLocale == null ? null : valueOf(defaultLocale.getLanguage());
    }

    private static InheritableThreadLocal<Locale> localeLocalThreadVariable = new InheritableThreadLocal<>();

    public static Locale getLocale() {
        return localeLocalThreadVariable.get();
    }

    public static void setLocale(final Locale locale) {
        localeLocalThreadVariable.set(locale);
    }

    public static Language getUserLanguage() {
        final Locale locale = getLocale();
        return locale != null ? valueOf(locale.getLanguage()) : null;
    }

    public static Language getLanguage() {
        final Language userLanguage = getUserLanguage();
        return userLanguage == null ? getDefaultLanguage() : userLanguage;
    }

}
