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

	private static InheritableThreadLocal<Locale> localeLocalThreadVariable = new InheritableThreadLocal<Locale>();

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
