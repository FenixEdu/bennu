package org.fenixedu.bennu.core.groups;

import java.util.Optional;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentNobodyGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

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
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent() && persistent.get().getCustomPresentationName() != null) {
            return persistent.get().getCustomPresentationName().getContent();
        }
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

    public Group underlyingGroup() {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get().getGroup().toGroup();
        }
        return NobodyGroup.get();
    }

    public Group underlyingGroup(DateTime when) {
        Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get().getGroup(when).toGroup();
        }
        return NobodyGroup.get();
    }

    public String getName() {
        return name;
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

    /**
     * Returns a {@link Mutator} that allows access to operations that will change the state
     * of this dynamic group.
     * 
     * @return
     *         A {@link Mutator} for this Dynamic Group.
     */
    public DynamicGroup.Mutator mutator() {
        return new Mutator();
    }

    /**
     * A {@link Mutator} allows access to operations that change the state of the Dynamic Group.
     * 
     * All the methods in this class behave like the ones in {@link Group}, only instead of returning
     * a new group, change the underlying one with the same semantics.
     * 
     */
    public class Mutator {

        /**
         * Changes the underlying group for this Dynamic Group.
         * 
         * @param group
         *            The new underlying group
         * @return
         *         This Dynamic Group, with the new underlying group.
         */
        public DynamicGroup changeGroup(Group group) {
            PersistentDynamicGroup.set(name, group.toPersistentGroup());
            return DynamicGroup.this;
        }

        /**
         * Changes the name for this Dynamic Group.
         * 
         * @param name
         *            The new name.
         * @return
         *         A new Dynamic Group, with a new name.
         */
        public DynamicGroup rename(String name) {
            Optional<PersistentDynamicGroup> persistent = PersistentDynamicGroup.getInstance(name);
            if (persistent.isPresent()) {
                persistent.get().rename(name);
            }
            return new DynamicGroup(name);
        }

        /**
         * Sets a custom presentation name for this dynamic group.
         * 
         * @param presentationName
         *            The new presentation name for this Dynamic Group.
         * @return
         *         This Dynamic Group, with the new presentation name.
         */
        public DynamicGroup setPresentationName(LocalizedString presentationName) {
            toPersistentGroup().changePresentationName(presentationName);
            return DynamicGroup.this;
        }

        public DynamicGroup and(Group group) {
            return changeGroup(underlyingGroup().and(group));
        }

        public DynamicGroup or(Group group) {
            return changeGroup(underlyingGroup().or(group));
        }

        public DynamicGroup minus(Group group) {
            return changeGroup(underlyingGroup().minus(group));
        }

        public DynamicGroup not() {
            return changeGroup(underlyingGroup().not());
        }

        public DynamicGroup grant(User user) {
            return changeGroup(underlyingGroup().grant(user));
        }

        public DynamicGroup revoke(User user) {
            return changeGroup(underlyingGroup().revoke(user));
        }
    }
}
