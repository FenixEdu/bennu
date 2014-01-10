package org.fenixedu.bennu.core.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {
    @Override
    public Response toResponse(DomainException exception) {
        return Response.status(exception.getResponseStatus()).type(MediaType.APPLICATION_JSON_TYPE)
                .entity(exception.asJson().toString()).build();
    }
}
