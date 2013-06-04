/*
 * PasswordRecoveryRequest.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class PasswordRecoveryRequest extends PasswordRecoveryRequest_Base {

    PasswordRecoveryRequest(final User user) {
        super();
        setRequested(new DateTime());
        setPrevious(user.getPasswordRecoveryRequest());
        setUser(user);
        setVerificationHash(calculateHash());
    }

    private String calculateHash() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getExternalId());
        builder.append(hashCode());
        builder.append(getRequested());
        builder.append(getUser().getUsername());
        builder.append(getUser().getExternalId());
        return DigestUtils.sha512Hex(builder.toString());
    }

    public String getRecoveryUrl(final String hostUrl) {
        final StringBuilder builder = new StringBuilder(hostUrl);
        builder.append("home.do?method=recoverPassword&passwordRecoveryRequestId=");
        builder.append(getExternalId());
        builder.append("&verificationHash=");
        builder.append(getVerificationHash());
        return builder.toString();
    }

    @Atomic
    public Boolean consume(final String verificationHash) {
        if (getConsumed() == null) {
            setConsumed(new DateTime());
            return getVerificationHash().equals(verificationHash);
        }
        return false;
    }

}
