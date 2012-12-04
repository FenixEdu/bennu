package module.webserviceutils.domain;

public class ClientHost extends ClientHost_Base {

    public ClientHost() {
	super();
	init();
	setEnabled(Boolean.TRUE);
    }

    public ClientHost(final String name, final String username, final String password, final String serverUrl) {
	super();
	init(name, username, password, Boolean.TRUE);
	setServerUrl(serverUrl);
    }

    @Override
    public void setHostSystem(final HostSystem hostSystem) {
	setClientHostSystem(hostSystem);
    }

    @Override
    protected void removeHostSystem() {
	removeClientHostSystem();
    }

    @Override
    public String getServerUrl() {
	final String serverUrl = super.getServerUrl();
	if (!serverUrl.endsWith("/")) {
	    return serverUrl.concat("/");
	}
	return serverUrl;
    }

}
