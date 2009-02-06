package myorg.domain;

import java.util.Set;

import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class VirtualHost extends VirtualHost_Base {

    private static final ThreadLocal<VirtualHost> threadVirtualHost = new ThreadLocal<VirtualHost>();

    public static VirtualHost getVirtualHostForThread() {
	return threadVirtualHost.get();
    }

    public static void setVirtualHostForThread(final VirtualHost virtualHost) {
	threadVirtualHost.set(virtualHost);
    }

    public static void releaseVirtualHostFromThread() {
	threadVirtualHost.remove();
    }

    public static VirtualHost setVirtualHostForThread(final String serverName) {
	final MyOrg myOrg = MyOrg.getInstance();
	final Set<VirtualHost> virtualHosts = myOrg.getVirtualHostsSet();
	for (final VirtualHost virtualHost : virtualHosts) {
	    if (serverName.equals(virtualHost.getHostname())) {
		setVirtualHostForThread(virtualHost);
		return virtualHost;
	    }
	}
	final VirtualHost virtualHost = virtualHosts.iterator().next();
	setVirtualHostForThread(virtualHost);
	return virtualHost;
    }

    public VirtualHost(final MyOrg myOrg) {
	setMyOrg(myOrg);
	setHostname("localhost");
	setApplicationTitle(new MultiLanguageString("MyOrg Application Title"));
	setApplicationSubTitle(new MultiLanguageString("MyOrg Application SubTitle"));
	setApplicationCopyright(new MultiLanguageString("My Organization Name"));
    }

    public VirtualHost(final VirtualHostBean virtualHostBean) {
	setMyOrg(MyOrg.getInstance());
	setHostname(virtualHostBean.getHostname());
	setApplicationTitle(virtualHostBean.getApplicationTitle());
	setApplicationSubTitle(virtualHostBean.getApplicationSubTitle());
	setApplicationCopyright(virtualHostBean.getApplicationCopyright());
    }

    @Service
    public static VirtualHost createVirtualHost(final VirtualHostBean virtualHostBean) {
	return new VirtualHost(virtualHostBean);
    }

    @Override
    public Theme getTheme() {
	Theme theme = super.getTheme();
	return theme != null ? theme : setAndReturnDefaultTheme();
    }

    @Service
    private Theme setAndReturnDefaultTheme() {
	Theme theme = getMyOrg().getThemes().get(0);
	setTheme(theme);
	return theme;
    }
}
