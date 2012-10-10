package module.webserviceutils.presentationTier.pages;

import module.webserviceutils.domain.ServerHost;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;

@SuppressWarnings("serial")
@EmbeddedComponent(path = "ServerHostManagement")
public class ServerHostManagement extends HostManagement<ServerHost> {
    private final static Object[] SERVER_HOST_PROPERTIES = new Object[] { "name", "clientAddresses", "username", "password", "enabled" };

    public ServerHostManagement() {
	super(ServerHost.class, "serverHosts", SERVER_HOST_PROPERTIES);
    }
}
