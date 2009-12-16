/*
 * @(#)DomainException.java
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

package myorg.domain.exceptions;

import java.util.ResourceBundle;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public class DomainException extends Error {

    private final String key;

    private final String[] args;

    private final String bundle;

    public DomainException() {
	this(null, (String[]) null);
    }

    public DomainException(final String key, final String... args) {
	super(key);
	this.key = key;
	this.args = args;
	this.bundle = null;
    }

    public DomainException(final String key, final Throwable cause, final String... args) {
	super(key, cause);
	this.key = key;
	this.args = args;
	this.bundle = null;
    }

    public DomainException(final String key, final String bundle) {
	super(key);
	this.key = key;
	this.bundle = bundle;
	this.args = null;
    }

    public DomainException(final String key, final String bundle, final String... args) {
	super(key);
	this.key = key;
	this.bundle = bundle;
	this.args = args;
    }

    public String getKey() {
	return key;
    }

    public String[] getArgs() {
	return args;
    }

    public String getBundle() {
	return bundle;
    }

    @Override
    public String getLocalizedMessage() {
	if (getBundle() == null) {
	    return getMessage();
	}
	return (getArgs() != null) ? getFormattedStringFromResourceBundle(getBundle(), getKey(), getArgs())
		: getStringFromResourceBundle(getBundle(), getKey());
    }

    /*
     * This actually has been copied from myorg.util.BundleUtil because we don't
     * want to add such deppency to DomainException. Although this code should
     * be refactored to a generic util, so it would be delegated instead of
     * being repeated.
     * 
     * PS: This code is repeated because bundleUtil deppends of
     * MultiLanguateString for example. And both me and LC felt that it made no
     * sence.
     */
    private String getStringFromResourceBundle(final String bundle, final String key) {
	final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, Language.getLocale());
	return resourceBundle.getString(key);
    }

    private String getFormattedStringFromResourceBundle(final String bundle, final String key, String... arguments) {
	String resourceString = getStringFromResourceBundle(bundle, key);
	for (int i = 0; i < arguments.length; i++) {
	    resourceString = resourceString.replace("{" + i + "}", arguments[i]);
	}
	return resourceString;
    }

}
