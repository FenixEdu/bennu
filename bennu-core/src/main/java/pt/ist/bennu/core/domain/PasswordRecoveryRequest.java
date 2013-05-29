/* 
* @(#)PasswordRecoveryRequest.java 
* 
* Copyright 2011 Instituto Superior Tecnico 
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
package pt.ist.bennu.core.domain;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Luis Cruz
 * 
 */
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
