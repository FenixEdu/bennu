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
import pt.ist.bennu.core.presentationTier.servlets.filters.FunctionalityFilter;
import pt.ist.bennu.core.presentationTier.servlets.filters.FunctionalityFilter.FunctionalityInfo;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author Nuno Diegues
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class ActionNode extends ActionNode_Base {

	public class InconsistentNodeFunctionality extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public InconsistentNodeFunctionality(ActionNode node, String methodAlias) {
			super("In the node attempted to be created with: \n\tPath: " + node.getPath() + "\n\tMethod: " + node.getMethod()
					+ "\n\tAlias: " + methodAlias);
		}

	}

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
		checkConsistencyInBranch();
	}

	public void checkConsistencyInBranch() {
		if (!hasFunctionality()) {
			return;
		}

		final FunctionalityInfo functionality = FunctionalityFilter.getFunctionality(getPath(), getMethod());
		Node parentNode = getParentNode();
		if (parentNode != null) {
			for (Node sibling : parentNode.getChildNodes()) {
				if (sibling != this) {
					FunctionalityFilter.checkForAliasConflict(functionality, sibling);
				}
			}
		}
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
		if (hasFunctionality()) {
			final Node parentNode = getParentNode();
			if (parentNode != null) {
				parentNode.appendUrlPrefix(stringBuilder);
			}
			if (stringBuilder.length() == 0) {
				// top level, whole semantic url
				stringBuilder.append(FunctionalityFilter.getSemanticURL(getPath(), getMethod()));
			} else {
				// child, just contributes with the method alias
				stringBuilder.append(FunctionalityFilter.getMethodAlias(getPath(), getMethod()));
			}
		} else {
			stringBuilder.append(getPath());
			stringBuilder.append(".do?method=");
			stringBuilder.append(getMethod());
		}
	}

	@Override
	public boolean isAcceptsFunctionality() {
		return true;
	}

	@Override
	public boolean hasFunctionality() {
		return FunctionalityFilter.hasSemanticURL(getPath(), getMethod());
	}

}
