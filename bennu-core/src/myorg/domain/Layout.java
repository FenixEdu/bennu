package myorg.domain;

import pt.ist.fenixWebFramework.services.Service;

public class Layout extends Layout_Base {

    public Layout(String name) {
	super();
	setMyOrg(MyOrg.getInstance());
	setName(name);
    }

    @Service
    public static Layout createLayout(String name) {
	return new Layout(name);
    }

    @Service
    public void delete() {
	removeMyOrg();
	for (VirtualHost virtualHost : getVirtualHostsSet()) {
	    removeVirtualHosts(virtualHost);
	}
	deleteDomainObject();
    }

    public static Layout getLayoutByName(String name) {
	for (Layout layout : MyOrg.getInstance().getLayoutSet()) {
	    if (layout.getName().equals(name)) {
		return layout;
	    }
	}
	return null;
    }
}
