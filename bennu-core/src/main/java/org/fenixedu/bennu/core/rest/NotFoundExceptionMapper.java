package org.fenixedu.bennu.core.rest;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.JsonObject;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        JsonObject json = new JsonObject();
        json.addProperty("message", "Resource not found");
        return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(json.toString()).build();
    }

}
