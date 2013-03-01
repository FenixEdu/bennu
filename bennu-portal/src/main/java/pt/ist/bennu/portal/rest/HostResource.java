package pt.ist.bennu.portal.rest;

import java.util.Collection;

import javax.ws.rs.Path;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.rest.DomainObjectResource;

@Path("hosts")
public class HostResource extends DomainObjectResource<VirtualHost> {

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

    @Override
    public Collection<VirtualHost> all() {
        return Bennu.getInstance().getVirtualHosts();
    }

    @Override
    public String collectionKey() {
        return "hosts";
    }

    @Override
    public Class<VirtualHost> type() {
        return VirtualHost.class;
    }

    @Override
    public boolean delete(VirtualHost obj) {
        obj.getInfo().delete();
        return true;
    }

}
