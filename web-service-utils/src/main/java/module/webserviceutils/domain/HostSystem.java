package module.webserviceutils.domain;

import module.webserviceutils.client.JerseyClient;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.fenixframework.Atomic;

public class HostSystem extends HostSystem_Base {

    private HostSystem() {
        super();
    }

    public static HostSystem getInstance() {
        if (!MyOrg.getInstance().hasHostSystem()) {
            initRemoteSystem();
        }
        return MyOrg.getInstance().getHostSystem();
    }

    @Atomic
    private static void initRemoteSystem() {
        MyOrg.getInstance().setHostSystem(new HostSystem());
    }

    public static ClientHost getClientHost(final String name) {
        for (final ClientHost host : getInstance().getClientHosts()) {
            if (host.getName().equals(name)) {
                return host;
            }
        }
        return null;
    }

    public static JerseyClient getJerseyClient(final String hostname) {
        final ClientHost clientHost = getClientHost(hostname);
        if (clientHost == null) {
            return null;
        }
        return new JerseyClient(clientHost);
    }

    public static JerseyClient getFenixJerseyClient() {
        return getJerseyClient("fenix");
    }

    public static ServerHost getServerByClientAddress(final String clientAddress) {
        for (final ServerHost serverHost : getInstance().getServerHosts()) {
            if (serverHost.hasClientAddress(clientAddress)) {
                return serverHost;
            }
        }
        return null;
    }

}
