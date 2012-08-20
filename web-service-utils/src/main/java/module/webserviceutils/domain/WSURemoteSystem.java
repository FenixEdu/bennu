package module.webserviceutils.domain;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

import module.webserviceutils.client.JerseyClient;

public class WSURemoteSystem extends WSURemoteSystem_Base {

    private WSURemoteSystem() {
	super();
    }

    public static WSURemoteSystem getInstance() {
	if (!MyOrg.getInstance().hasRemoteSystem()) {
	    initRemoteSystem();
	}
	return MyOrg.getInstance().getRemoteSystem();
    }

    @Service
    private static void initRemoteSystem() {
	MyOrg.getInstance().setRemoteSystem(new WSURemoteSystem());
    }

    public static WSURemoteHost getRemoteHost(final String name) {
	for (WSURemoteHost host : getInstance().getRemoteHosts()) {
	    if (host.getName().equals(name)) {
		return host;
	    }
	}
	return null;
    }

    public static JerseyClient getJerseyClient(final String hostname) {
	final WSURemoteHost remoteHost = getRemoteHost(hostname);
	if (remoteHost == null) {
	    return null;
	}
	return new JerseyClient(remoteHost);
    }

    public static JerseyClient getFenixJerseyClient() {
	return getJerseyClient("fenix");
    }

}
