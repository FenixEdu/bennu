package pt.ist.bennu.portal.domain;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.service.Service;

public class HostInfo extends HostInfo_Base {

    private HostInfo() {
        super();
    }

    public HostInfo(VirtualHost virtualHost) {
        this();
        setHost(virtualHost);
    }

    @Service
    public void delete() {
        final VirtualHost host = getHost();
        host.removeInfo();
        host.delete();
        if (host.hasMenu()) {
            host.getMenu().delete();
        }
        deleteDomainObject();
    }

}
