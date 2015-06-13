package org.fenixedu.bennu.core.domain;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.groups.GroupConstant;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentUserGroup;
import org.fenixedu.bennu.core.groups.Group;

public class BennuGroupIndex {

    public static <T extends GroupConstant> Stream<T> groupConstant(Class<T> type) {
        return Bennu.getInstance().getGroupConstantSet().stream().filter(g -> type.isInstance(g)).map(type::cast);
    }

    public static Set<PersistentUserGroup> getUserGroups(User user) {
        return user.getUserGroupSet();
    }

    public static Optional<PersistentDynamicGroup> dynamic(String name) {
        return Bennu.getInstance().getDynamicSet().stream().filter(g -> g.getName().equals(name)).findAny();
    }

    public static Stream<Group> allDynamicGroups() {
        return Bennu.getInstance().getDynamicSet().stream().map(PersistentGroup::toGroup);
    }

    public static boolean isUserGroupMember(User user, PersistentUserGroup group) {
        return user.getUserGroupSet().contains(group);
    }
}
