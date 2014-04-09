package org.fenixedu.bennu.core.domain;

import java.util.Set;

import org.fenixedu.bennu.core.domain.groups.GroupConstant;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentUserGroup;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class BennuGroupIndex {
    public static <T extends GroupConstant> T getGroupConstant(Class<T> type) {
        return FluentIterable.from(Bennu.getInstance().getGroupConstantSet()).filter(type).first().orNull();
    }

    public static Set<PersistentUserGroup> getUserGroups(User user) {
        return user.getUserGroupSet();
    }

    public static Optional<PersistentDynamicGroup> getDynamic(String name) {
        for (PersistentDynamicGroup dynamic : Bennu.getInstance().getDynamicSet()) {
            if (dynamic.getName().equals(name)) {
                return Optional.of(dynamic);
            }
        }
        return Optional.absent();
    }
}