package myorg.domain.groups;

import java.util.Set;

import myorg.domain.User;
import myorg.domain.exceptions.DomainException;
import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.services.Service;

public class NegationGroup extends NegationGroup_Base {

    public NegationGroup(PersistentGroup persistentGroup) {
	super();
	setPersistentGroup(persistentGroup);
    }

    @Override
    public Set<User> getMembers() {
	throw new DomainException();
    }

    @Override
    public String getName() {
	return ("NOT " + getPersistentGroup().getName());
    }

    @Override
    public boolean isMember(User user) {
	return !getPersistentGroup().isMember(user);
    }

    @Service
    public static NegationGroup createNegationGroup(final PersistentGroup persistentGroup) {
	return new NegationGroup(persistentGroup);
    }
}
