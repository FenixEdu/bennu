package org.fenixedu.bennu.core.groups;

import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentNobodyGroup;
import org.joda.time.DateTime;

import com.google.common.base.Optional;

/**
 * Named link to a group.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @see Group
 */
public final class DynamicGroup extends Group {
    private static final long serialVersionUID = -8544141315269000405L;

    private final String name;

    private DynamicGroup(String name) {
        super();
        this.name = name;
    }

    public static DynamicGroup get(String name) {
        return new DynamicGroup(name);
    }

    public boolean isDefined() {
        return PersistentDynamicGroup.getInstance(name).isPresent();
    }

    public DynamicGroup rename(String name) {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            persistent.get().rename(name);
        }
        return new DynamicGroup(name);
    }

    public DynamicGroup changeGroup(Group group) {
        PersistentDynamicGroup.set(name, group.toPersistentGroup());
        return this;
    }

    @Override
    public PersistentDynamicGroup toPersistentGroup() {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get();
        }
        return PersistentDynamicGroup.set(name, PersistentNobodyGroup.getInstance());
    }

    @Override
    public String getPresentationName() {
        return name + ": (" + underlyingGroup().getPresentationName() + ")";
    }

    @Override
    public String getExpression() {
        return "#" + name;
    }

    @Override
    public Set<User> getMembers() {
        return underlyingGroup().getMembers();
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return underlyingGroup(when).getMembers(when);
    }

    @Override
    public boolean isMember(User user) {
        return underlyingGroup().isMember(user);
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return underlyingGroup(when).isMember(user, when);
    }

    private Group underlyingGroup() {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get().getGroup().toGroup();
        }
        return NobodyGroup.get();
    }

    private Group underlyingGroup(DateTime when) {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get().getGroup(when).toGroup();
        }
        return NobodyGroup.get();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DynamicGroup) {
            return name.equals(((DynamicGroup) object).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
