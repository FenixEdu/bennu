package myorg.domain.contents;

import myorg.domain.VirtualHost;
import myorg.domain.groups.PersistentGroup;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class LinkNode extends LinkNode_Base {
    
    public LinkNode(final VirtualHost virtualHost, final Node parentNode, final String url) {
        super();
        init(virtualHost, parentNode, null);
        setUrl(url);
    }

    @Service
    public static LinkNode createLinkNode(final VirtualHost virtualHost, final Node parentNode, final String url,
	    final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	final LinkNode linkNode = new LinkNode(virtualHost, parentNode, url);
	linkNode.setLinkBundle(bundle);
	linkNode.setLinkKey(key);
	linkNode.setAccessibilityGroup(accessibilityGroup);
	return linkNode;
    }

    @Override
    protected void appendUrlPrefix(final StringBuilder stringBuilder) {
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
    
}
