/*
 * @(#)ActionNode.java
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

package myorg.domain.contents;

import myorg.domain.VirtualHost;
import myorg.domain.groups.PersistentGroup;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class ActionNode extends ActionNode_Base {
    
    public ActionNode(final VirtualHost virtualHost, final Node node) {
        super();
        init(virtualHost, node, null);
    }

    @Service
    public static ActionNode createActionNode(final VirtualHost virtualHost, final Node node, final String path,
	    final String method, final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	final ActionNode actionNode = new ActionNode(virtualHost, node);
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
    protected void appendUrlPrefix(final StringBuilder stringBuilder) {
	stringBuilder.append(getPath());
	stringBuilder.append(".do?method=");
	stringBuilder.append(getMethod());
    }

}
