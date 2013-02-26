package pt.ist.bennu.portal.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;

@Path("hosts")
public class HostResource extends BennuRestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list() {
        return view(Bennu.getInstance().getVirtualHosts(), "hosts");
    }

    /* {    
            "hostname" : "myapp.com",
            "applicationTitle" : {"pt" : "my app"},
            "htmlTitle" : {"pt" : "app html title"},
            "applicationSubTitle" : {"pt" : "app subtitle"},
            "applicationCopyright" : {"pt" : "app copyright"},
            "supportEmailAddress" : "email@myapp.com",
            "systemEmailAddress" : "system@myapp.com" 
        }
     */

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@FormParam("model") String jsonData) {
        return view(create(jsonData, VirtualHost.class));
    }

    @PUT
    @Path("{oid}")
    public String update(@PathParam("oid") String oid, @FormParam("model") String jsonData) {
        return view(update(jsonData, readDomainObject(oid)));
    }
}
