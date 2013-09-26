/*
 * @(#)EmailAddressList.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the E-mail SMTP Adapter Module.
 *
 *   The E-mail SMTP Adapter Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The E-mail Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the E-mail Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.email.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class EmailAddressList implements Serializable {

    private final String emailAddresses;

    public EmailAddressList(final String emailAddresses) {
        super();
        this.emailAddresses = emailAddresses == null || emailAddresses.length() == 0 ? null : emailAddresses;
    }

    public EmailAddressList(final Collection<String> emailAddressCollection) {
        super();
        final StringBuilder emailAddresses = new StringBuilder();
        if (emailAddressCollection != null) {
            for (final String emailAddress : emailAddressCollection) {
                final String emailAddressTrimmed = emailAddress.trim();
                if (emailAddresses.length() > 0) {
                    emailAddresses.append(", ");
                }

                emailAddresses.append(emailAddressTrimmed);
            }
        }
        this.emailAddresses = emailAddresses.length() == 0 ? null : emailAddresses.toString();
    }

    @Override
    public String toString() {
        return emailAddresses;
    }

    public String[] toArray() {
        return emailAddresses == null ? null : emailAddresses.split(", ");
    }

    public Collection<String> toCollection() {
        if (emailAddresses == null) {
            return null;
        }
        final Collection<String> collection = new ArrayList<String>();
        for (final String emailAddress : toArray()) {
            collection.add(emailAddress);
        }
        return collection;
    }

    public boolean isEmpty() {
        return emailAddresses == null || emailAddresses.length() == 0;
    }

    public int size() {
        return toArray().length;
    }

}
