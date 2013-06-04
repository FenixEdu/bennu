package pt.ist.bennu.core.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import jvstm.cps.ConsistencyException;

import com.google.gson.JsonObject;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Provider
public class ConsistencyExceptionMapper implements ExceptionMapper<ConsistencyException> {
    @Override
    public Response toResponse(ConsistencyException exception) {
        JsonObject json = new JsonObject();
        json.addProperty("message", exception.getLocalizedMessage());
        return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(json.toString())
                .build();
    }
}
