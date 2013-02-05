/* 
* @(#)LinkNode.java 
* 
* Copyright 2010 Instituto Superior Tecnico 
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
package pt.ist.bennu.core.domain.contents;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.groups.PersistentGroup;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author Nuno Diegues
 * @author Luis Cruz
 * 
 */
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

    @Override
    public boolean isAcceptsFunctionality() {
        return false;
    }

}
