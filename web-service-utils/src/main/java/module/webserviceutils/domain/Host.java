package module.webserviceutils.domain;

import pt.ist.fenixWebFramework.services.Service;

public abstract class Host extends Host_Base {

    protected void init() {
	setHostSystem(HostSystem.getInstance());
    }

    protected void init(final String name, final String username, final String password, final Boolean enabled) {
	init();
	setName(name);
	setUsername(username);
	setPassword(password);
	setEnabled(enabled);
    }

    public Host() {
	super();
	init();
    }

    public Host(final String name, final String username, final String password, final Boolean enabled) {
	this();
	init(name, username, password, enabled);
    }

    public Host(final String name, final String username, final String password) {
	this(name, username, password, Boolean.TRUE);
    }

    public abstract void setHostSystem(HostSystem hostSystem);

    @Service
    public void delete() {
	removeHostSystem();
	deleteDomainObject();
    }

    protected abstract void removeHostSystem();

    public boolean isEnabled() {
	return getEnabled();
    }

    @Override
    public Boolean getEnabled() {
	final Boolean enabled = super.getEnabled();
	return enabled != null && enabled;
    }

}
