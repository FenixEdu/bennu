/* 
* @(#)DomainException.java 
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
package pt.ist.bennu.core.domain.exceptions;

import java.util.ResourceBundle;

import pt.ist.fenixframework.FFDomainException;
import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author João Antunes
 * @author Pedro Santos
 * @author Sérgio Silva
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class DomainException extends FFDomainException {

	private final String key;

	private final String[] args;

	private final ResourceBundle bundle;

	public DomainException() {
		this(null, (String[]) null);
	}

	public DomainException(final String key, final String... args) {
		super(key);
		this.key = key;
		this.args = args;
		this.bundle = null;
	}

	public DomainException(final String key, final ResourceBundle bundle, final String... args) {
		super(key);
		this.key = key;
		this.bundle = bundle;
		this.args = args;
	}

	public DomainException(final String key, final Throwable cause, final String... args) {
		super(key, cause);
		this.key = key;
		this.args = args;
		this.bundle = null;
	}

	public DomainException(final String key, final Throwable cause, final ResourceBundle bundle, final String... args) {
		super(key, cause);
		this.key = key;
		this.args = args;
		this.bundle = bundle;
	}

	public String getKey() {
		return key;
	}

	public String[] getArgs() {
		return args;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	@Override
	public String getLocalizedMessage() {
		if (getBundle() == null) {
			return getMessage();
		}
		String toReturn = null;
		try {
			toReturn = (getArgs() != null) ? getFormattedStringFromResourceBundle() : getStringFromResourceBundle();
		} catch (java.util.MissingResourceException exception) {
			toReturn = getMessage();
		}
		return toReturn;
	}

	/*
	 * This actually has been copied from pt.ist.bennu.core.util.BundleUtil because we don't
	 * want to add such deppency to DomainException. Although this code should
	 * be refactored to a generic util, so it would be delegated instead of
	 * being repeated.
	 * 
	 * PS: This code is repeated because bundleUtil deppends of
	 * MultiLanguateString for example. And both me and LC felt that it made no
	 * sns
	 */
	private String getStringFromResourceBundle() {
		return getBundle().getString(getKey());
	}

	private String getFormattedStringFromResourceBundle() {
		String resourceString = getStringFromResourceBundle();
		String[] arguments = getArgs();
		for (int i = 0; i < arguments.length; i++) {
			resourceString = resourceString.replace("{" + i + "}", arguments[i]);
		}
		return resourceString;
	}

	public static ResourceBundle getResourceFor(String resourceName) {
		return ResourceBundle.getBundle(resourceName, Language.getLocale());
	}

}
