package module.webserviceutils.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

import module.webserviceutils.client.JerseyClient;

public class WSURemoteSystem extends WSURemoteSystem_Base {

    private static Map<WSURemoteHost, JerseyClient> CLIENTS;

    static {
	CLIENTS = new ConcurrentHashMap<WSURemoteHost, JerseyClient>();
    }

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
	JerseyClient jerseyClient = CLIENTS.get(remoteHost);
	if (jerseyClient == null) {
	    jerseyClient = new JerseyClient(remoteHost);
	    CLIENTS.put(remoteHost, jerseyClient);
	}
	return jerseyClient;
    }

    public static JerseyClient getFenixJerseyClient() {
	return getJerseyClient("fenix");
    }

    public static void removeRemoteHost(WSURemoteHost remoteHost) {
	CLIENTS.remove(remoteHost);
    }
}
