package pt.ist.bennu.bennu.core.rest.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
                .entity(BennuRestError.RESOURCE_NOT_FOUND.asJsonString()).build();
    }

}
