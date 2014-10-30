package org.fenixedu.bennu.core.domain;

import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.groups.GroupConstant;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentUserGroup;
import org.fenixedu.bennu.core.groups.Group;

import com.google.common.base.Optional;

public class BennuGroupIndex {
    @Deprecated
    public static <T extends GroupConstant> T getGroupConstant(Class<T> type) {
        return groupConstant(type).findAny().orElse(null);
    }

    public static <T extends GroupConstant> Stream<T> groupConstant(Class<T> type) {
        return (Stream<T>) Bennu.getInstance().getGroupConstantSet().stream().filter(g -> type.isInstance(g));
    }

    public static Set<PersistentUserGroup> getUserGroups(User user) {
        return user.getUserGroupSet();
    }

    @Deprecated
    public static Optional<PersistentDynamicGroup> getDynamic(String name) {
        for (PersistentDynamicGroup dynamic : Bennu.getInstance().getDynamicSet()) {
            if (dynamic.getName().equals(name)) {
                return Optional.of(dynamic);
            }
        }
        return Optional.absent();
    }

    public static java.util.Optional<PersistentDynamicGroup> dynamic(String name) {
        return Bennu.getInstance().getDynamicSet().stream().filter(g -> g.getName().equals(name)).findAny();
    }

    public static Stream<Group> allDynamicGroups() {
        return Bennu.getInstance().getDynamicSet().stream().map(PersistentGroup::toGroup);
    }
}
