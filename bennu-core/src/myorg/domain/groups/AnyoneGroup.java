package myorg.domain.groups;

import myorg.domain.User;
import pt.ist.fenixWebFramework.services.Service;

public class AnyoneGroup extends AnyoneGroup_Base {

    private AnyoneGroup() {
        super();
    }

    @Override
    public boolean isMember(final User user) {
	return true;
    }

    @Service
    public static AnyoneGroup getInstance() {
	final AnyoneGroup anyoneGroup = (AnyoneGroup) PersistentGroup.getInstance(AnyoneGroup.class);
	return anyoneGroup == null ? new AnyoneGroup() : anyoneGroup;
    }

}
