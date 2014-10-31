package org.fenixedu.bennu.core.domain.groups;

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.UserGroup;

class DomainBackedUserGroup extends UserGroup {

    private static final long serialVersionUID = -7112128919581842609L;

    private final PersistentUserGroup userGroup;

    DomainBackedUserGroup(PersistentUserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    public PersistentUserGroup toPersistentGroup() {
        return userGroup;
    }

    @Override
    public Set<User> getMembers() {
        return Collections.unmodifiableSet(userGroup.getMemberSet());
    }

    @Override
    protected Set<User> members() {
        return userGroup.getMemberSet();
    }

    @Override
    public boolean isMember(User user) {
        if (user == null) {
            return false;
        }
        if (userGroup.getMemberSet().size() > 100) {
            return BennuGroupIndex.isUserGroupMember(user, userGroup);
        } else {
            return members().contains(user);
        }
    }

    @Override
    public String getExpression() {
        return "U(" + getPresentationName() + ")";
    }
}
