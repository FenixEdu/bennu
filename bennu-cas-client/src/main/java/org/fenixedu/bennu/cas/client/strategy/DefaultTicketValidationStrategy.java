/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2016 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2016 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 *
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.cas.client.strategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.cas.client.CASClientConfiguration;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

public class DefaultTicketValidationStrategy implements TicketValidationStrategy {

    private static final String CALLBACK_URL = "CALLBACK_URL";
    private final TicketValidator validator =
            new Cas20ServiceTicketValidator(CASClientConfiguration.getConfiguration().casServerUrl());

    @Override
    public void validateTicket(final String ticket, final String requestURL, final HttpServletRequest request,
            final HttpServletResponse response) throws TicketValidationException, AuthorizationException {

        // CALLBACK_URL is the attribute that OMNIS.cloud platform redirector system uses to understand
        // the users callback. Since we are destroying a session and creating a new one when validating
        // the ticket, we need to hop the value from one session to the other.
        //
        // 3 February 2023 - Paulo Abrantes
        String redirect = (String) request.getSession(false).getAttribute(CALLBACK_URL);
        Authenticate.logout(request, response);
        String username = validator.validate(ticket, requestURL).getPrincipal().getName();
        User user = User.findByUsername(username);
        Authenticate.login(request, response, user, "TODO: CHANGE ME");
        if (redirect != null) {
            request.getSession(false).setAttribute(CALLBACK_URL, redirect);
        }

    }
}
