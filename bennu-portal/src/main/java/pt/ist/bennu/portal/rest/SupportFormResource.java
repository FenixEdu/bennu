/*
 * SupportFormResource.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-portal.
 *
 * bennu-portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.portal.rest;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.email.EmailSystem;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("support")
public class SupportFormResource extends BennuRestResource {
    private static final Logger logger = LoggerFactory.getLogger(SupportFormResource.class);

    @POST
    @Path("errorreport")
    @Produces(MediaType.APPLICATION_JSON)
    public void errorReport(@FormParam("model") final String json) {
        try {
            JsonObject model = new JsonParser().parse(json).getAsJsonObject();

            StringBuilder builder = new StringBuilder();
            builder.append(model.get("comment").getAsString());

            builder.append("\n\n- - - - - - - - - - - Error Origin - - - - - - - - - - -\n");
            builder.append("[UserLoggedIn] " + (Authenticate.getUser() != null ? Authenticate.getUser().getUsername() : "none")
                    + "\n");
            builder.append("[RequestURI] " + (model.has("request-uri") ? model.get("request-uri").getAsString() : "unkown")
                    + "\n");
            builder.append("[RequestURL] " + (model.has("request-url") ? model.get("request-url").getAsString() : "unknown")
                    + "\n");
            builder.append("[QueryString] "
                    + (model.has("requestq-query") ? model.get("request-query").getAsString() : "unknown") + "\n");

            builder.append("[StackTrace] " + (model.has("stacktrace") ? model.get("stacktrace").getAsString() : "unknown") + "\n");

            InternetAddress from = null;
            if (Authenticate.getUser() != null && Authenticate.getUser().getEmail() != null) {
                from = new InternetAddress(Authenticate.getUser().getEmail(), Authenticate.getUser().getPresentationName());
            }
            List<InternetAddress> to =
                    Collections
                            .singletonList(new InternetAddress(VirtualHost.getVirtualHostForThread().getSupportEmailAddress()));
            MimeBodyPart part = new MimeBodyPart();
            part.setText(builder.toString());

            EmailSystem.send(EmailSystem.message(EmailSystem.emailSession(), from, to, null, null, "[Excepção]", part));
        } catch (UnsupportedEncodingException | MessagingException e) {
            logger.error("Could not send support email", e);
        }
    }
}
