/*
 * @(#)DomainException.java
 * 
 * Copyright 2009 Instituto Superior Tecnico Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 * https://fenix-ashes.ist.utl.pt/
 * 
 * This file is part of the Bennu Web Application Infrastructure.
 * 
 * The Bennu Web Application Infrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Bennu is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Bennu. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain.exceptions;

import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.fenixframework.FFDomainException;

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

	private final String bundle;

	public DomainException() {
		this(null, null);
	}

	public DomainException(String bundle, String key, String... args) {
		super(key);
		this.bundle = bundle;
		this.key = key;
		this.args = args;
	}

	public DomainException(Throwable cause, String bundle, String key, String... args) {
		super(cause);
		this.bundle = bundle;
		this.key = key;
		this.args = args;
	}

	@Override
	public String getLocalizedMessage() {
		if (key != null) {
			return BundleUtil.getString(bundle, key, args);
		}
		return super.getLocalizedMessage();
	}
}
