/*
 * ThrowableExceptionMapper.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.ist.bennu.core.security.Authenticate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(Throwable exception) {
        JsonObject json = new JsonObject();
        if (Authenticate.getUser() != null) {
            json.addProperty("user", Authenticate.getUser().getUsername());
        }
        json.addProperty("request-uri", request.getRequestURI());
        json.addProperty("request-url", request.getRequestURL().toString());
        json.addProperty("request-query", request.getQueryString());

        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        json.addProperty("stacktrace", errors.toString());

        return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(gson.toJson(json))
                .build();
    }
}
