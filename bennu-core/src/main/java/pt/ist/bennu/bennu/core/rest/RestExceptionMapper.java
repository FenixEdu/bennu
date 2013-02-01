package pt.ist.bennu.bennu.core.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Provider
public class RestExceptionMapper implements ExceptionMapper<RestException> {

	@Override
	public Response toResponse(RestException exception) {
		return Response.status(exception.getError().getStatus()).type(MediaType.APPLICATION_JSON)
				.entity(exception.asJsonString()).build();
	}
}
