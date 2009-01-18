package myorg.domain.groups;

import java.util.Set;

import myorg.domain.User;

public abstract class People extends People_Base {

    public People() {
        super();
    }

    @Override
    public Set<User> getMembers() {
	return getUsersSet();
    }

}
