package pt.ist.bennu.bennu.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/settings")
public class SettingsResource extends AbstractResource {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getSettings() {
		return Response.ok().entity(serialize(getCasConfigContext())).build();
	}

}
