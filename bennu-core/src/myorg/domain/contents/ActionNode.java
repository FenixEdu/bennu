/*
 * @(#)ActionNode.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
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

package myorg.domain.contents;

import myorg.domain.VirtualHost;
import myorg.domain.groups.PersistentGroup;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class ActionNode extends ActionNode_Base {

    public ActionNode() {
	super();
    }

    public ActionNode(final VirtualHost virtualHost, final Node node, final String path, final String method,
	    final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	super();
	init(virtualHost, node, path, method, bundle, key, accessibilityGroup);
    }

    protected void init(final VirtualHost virtualHost, final Node node, final String path, final String method,
	    final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	init(virtualHost, node, null);
	setPath(path);
	setMethod(method);
	setLinkBundle(bundle);
	setLinkKey(key);
	setAccessibilityGroup(accessibilityGroup);
    }

    @Service
    public static ActionNode createActionNode(final VirtualHost virtualHost, final Node node, final String path,
	    final String method, final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	return new ActionNode(virtualHost, node, path, method, bundle, key, accessibilityGroup);
    }

    @Override
    public Object getElement() {
	return null;
    }

    @Override
    public MultiLanguageString getLink() {
	final String bundle = getLinkBundle();
	final String key = getLinkKey();
	try {
	    return BundleUtil.getMultilanguageString(bundle, key);
	} catch (java.util.MissingResourceException e) {
	    e.printStackTrace();
	    return new MultiLanguageString(getLinkKey());
	}
    }

    @Override
    protected void appendUrlPrefix(final StringBuilder stringBuilder) {
	stringBuilder.append(getPath());
	stringBuilder.append(".do?method=");
	stringBuilder.append(getMethod());
    }

}
