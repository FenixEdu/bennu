package pt.ist.bennu.portal.domain;

import pt.ist.bennu.core.domain.VirtualHost;

public class HostInfo extends HostInfo_Base {

    private HostInfo() {
        super();
    }

    public HostInfo(VirtualHost virtualHost) {
        this();
        setHost(virtualHost);
    }

}
