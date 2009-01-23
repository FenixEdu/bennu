package myorg.domain.contents;

import java.util.Locale;
import java.util.ResourceBundle;

import myorg.domain.groups.PersistentGroup;
import myorg.presentationTier.Context;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class ActionNode extends ActionNode_Base {
    
    public ActionNode() {
        super();
        init(null, null);
    }

    @Service
    public static ActionNode createActionNode(final String path, final String method, final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	final ActionNode actionNode = new ActionNode();
	actionNode.setPath(path);
	actionNode.setMethod(method);
	actionNode.setLinkBundle(bundle);
	actionNode.setLinkKey(key);
	actionNode.setAccessibilityGroup(accessibilityGroup);
	return actionNode;
    }

    @Override
    public Object getElement() {
	return null;
    }

    @Override
    public MultiLanguageString getLink() {
	final MultiLanguageString multiLanguageString = new MultiLanguageString();
	final String bundle = getLinkBundle();
	final String key = getLinkKey();
	if (!addContent(multiLanguageString, bundle, Language.getLocale(), key)) {
	    if (!addContent(multiLanguageString, bundle, Language.getDefaultLocale(), key)) {
		addContent(multiLanguageString, bundle, null, key);
	    }
	}
	return multiLanguageString;
    }

    private boolean addContent(final MultiLanguageString multiLanguageString, final String bundle, final Locale locale, final String key) {
	final ResourceBundle resourceBundle;
	if (locale == null) {
	    resourceBundle = ResourceBundle.getBundle(getLinkBundle());
	} else {
	    resourceBundle = ResourceBundle.getBundle(getLinkBundle(), Language.getLocale());
	}
	if (resourceBundle != null) {
	    final Language language = Language.valueOf(locale.getLanguage());
	    final String content = resourceBundle.getString(key);
	    multiLanguageString.setContent(language, content);
	    return true;
	}
	return false;
    }

    @Override
    public String getUrl(final Context context) {
	return getPath() + ".do?method=" + getMethod();
    }

}
