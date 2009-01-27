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
