package pt.ist.bennu.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.bennu.core.domain.Bennu;

@Path("/users")
public class UserResource extends AbstractResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		verifyAndGetRequestAuthor();
		return Response.ok(serialize(Bennu.getInstance().getUsersSet(), "users")).build();
	}

	@GET
	@Path("/{oid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("oid") String externalId) {
		return Response.ok(serializeFromExternalId(externalId)).build();
	}
}