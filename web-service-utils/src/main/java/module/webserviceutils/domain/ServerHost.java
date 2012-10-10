package module.webserviceutils.domain;

import org.apache.commons.lang.StringUtils;

import pt.utl.ist.fenix.tools.util.Strings;

public class ServerHost extends ServerHost_Base {

    public ServerHost() {
	super();
	init();
    }

    public ServerHost(final String name, final String username, final String password, final String... clientAddresses) {
	super();
	init(name, username, password, Boolean.TRUE);
	setClientAddresses(clientAddresses);
    }

    private void setClientAddresses(final String[] clientAddresses) {
	setClientAddresses(new Strings(clientAddresses));
    }

    public boolean matches(final String hostAddress, final String username, final String password) {
	if (StringUtils.isBlank(hostAddress) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
	    return false;
	}
	return getClientAddresses().contains(hostAddress) && username.equalsIgnoreCase(getUsername())
		&& password.equals(getPassword());
    }

    @Override
    public void setHostSystem(final HostSystem hostSystem) {
	setServerHostSystem(hostSystem);
    }

    @Override
    protected void removeHostSystem() {
	removeServerHostSystem();
    }

    public boolean hasClientAddress(final String clientAddress) {
	return getClientAddresses().contains(clientAddress);
    }

    public boolean matches(final String username, final String password) {
	return getUsername().equals(username) && getPassword().equals(password);
    }

}
