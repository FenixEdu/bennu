/*
 * @(#)UserAutoComplete.java
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

package myorg.presentationTier.renderers.autoCompleteProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import myorg.domain.MyOrg;
import myorg.domain.User;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public class UserAutoComplete implements AutoCompleteProvider {

    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	Set<User> users = new HashSet<User>();
	String[] values = StringNormalizer.normalize(value).toLowerCase().split(" ");
	for (User user : MyOrg.getInstance().getUserSet()) {
	    final String normalizedUser = StringNormalizer.normalize(user.getPresentationName()).toLowerCase();

	    if (hasMatch(values, normalizedUser)) {
		users.add(user);
	    }
	}
	return users;
    }

    private boolean hasMatch(String[] input, String personNameParts) {
	for (final String namePart : input) {
	    if (personNameParts.indexOf(namePart) == -1) {
		return false;
	    }
	}
	return true;
    }

}
