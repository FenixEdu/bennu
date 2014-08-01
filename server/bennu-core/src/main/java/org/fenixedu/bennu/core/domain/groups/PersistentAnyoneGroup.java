package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;

public class PersistentAnyoneGroup extends PersistentAnyoneGroup_Base {
    protected PersistentAnyoneGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return AnyoneGroup.get();
    }

    /**
     * Get or create singleton instance of {@link PersistentAnyoneGroup}
     * 
     * @return singleton {@link PersistentAnyoneGroup} instance
     */
    public static PersistentAnyoneGroup getInstance() {
        return singleton(() -> find(PersistentAnyoneGroup.class), () -> new PersistentAnyoneGroup());
    }
}
