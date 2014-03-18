package org.fenixedu.bennu.core.domain;

import java.util.Set;

import org.fenixedu.bennu.core.domain.groups.AnonymousGroup;
import org.fenixedu.bennu.core.domain.groups.AnyoneGroup;
import org.fenixedu.bennu.core.domain.groups.DynamicGroup;
import org.fenixedu.bennu.core.domain.groups.DynamicGroup.DynamicGroupNotFoundException;
import org.fenixedu.bennu.core.domain.groups.LoggedGroup;
import org.fenixedu.bennu.core.domain.groups.NobodyGroup;
import org.fenixedu.bennu.core.domain.groups.UserGroup;

import com.google.common.collect.FluentIterable;

public class BennuGroupIndex {
    public static AnonymousGroup getAnonymous() {
        return FluentIterable.from(Bennu.getInstance().getGroupConstantSet()).filter(AnonymousGroup.class).first().orNull();
    }

    public static AnyoneGroup getAnyone() {
        return FluentIterable.from(Bennu.getInstance().getGroupConstantSet()).filter(AnyoneGroup.class).first().orNull();
    }

    public static LoggedGroup getLogged() {
        return FluentIterable.from(Bennu.getInstance().getGroupConstantSet()).filter(LoggedGroup.class).first().orNull();
    }

    public static NobodyGroup getNobody() {
        return FluentIterable.from(Bennu.getInstance().getGroupConstantSet()).filter(NobodyGroup.class).first().orNull();
    }

    public static Set<UserGroup> getUserGroups(User user) {
        return user.getUserGroupSet();
    }

    public static DynamicGroup getDynamic(String name) {
        for (DynamicGroup dynamic : Bennu.getInstance().getDynamicSet()) {
            if (dynamic.getName().equals(name)) {
                return dynamic;
            }
        }
        throw DynamicGroupNotFoundException.dynamicGroupNotFound(name);
    }
}