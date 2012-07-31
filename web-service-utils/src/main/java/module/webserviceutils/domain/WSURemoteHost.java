package module.webserviceutils.domain;

import pt.ist.fenixWebFramework.services.Service;

public class WSURemoteHost extends WSURemoteHost_Base {

    public WSURemoteHost() {
	super();
	setRemoteSystem(WSURemoteSystem.getInstance());
    }

    public WSURemoteHost(String name, String url, String username, String password, Boolean allowInvocationAccess) {
	this();
	setName(name);
	setUrl(url);
	setUsername(username);
	setPassword(password);
	setAllowInvocationAccess(allowInvocationAccess);
    }

    public WSURemoteHost(String name, String url, String username, String password) {
	this(name, url, username, password, Boolean.TRUE);
    }

    @Service
    public void delete() {
	WSURemoteSystem.removeRemoteHost(this);
	removeRemoteSystem();
	deleteDomainObject();
    }
}
