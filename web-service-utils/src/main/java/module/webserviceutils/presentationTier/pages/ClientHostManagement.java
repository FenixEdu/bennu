package module.webserviceutils.presentationTier.pages;

import module.webserviceutils.domain.ClientHost;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;

@SuppressWarnings("serial")
@EmbeddedComponent(path = "ClientHostManagement")
public class ClientHostManagement extends HostManagement<ClientHost> {
    private final static Object[] CLIENT_HOST_PROPERTIES = new Object[] { "name", "serverUrl", "username", "password", "enabled" };

    public ClientHostManagement() {
	super(ClientHost.class, "clientHosts", CLIENT_HOST_PROPERTIES);
    }
}