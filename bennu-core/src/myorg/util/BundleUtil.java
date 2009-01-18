package myorg.util;

import java.util.ResourceBundle;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public class BundleUtil {

    public static String getStringFromResourceBundle(final String bundle, final String key) {
	final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, Language.getLocale());
	return resourceBundle.getString(key);	
    }

}
