package myorg.domain.contents;

import myorg.domain.groups.PersistentGroup;
import myorg.presentationTier.Context;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;
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
	final String bundle = getLinkBundle();
	final String key = getLinkKey();
	return BundleUtil.getMultilanguageString(bundle, key);
    }

    @Override
    public String getUrl(final Context context) {
	return getPath() + ".do?method=" + getMethod();
    }

}
