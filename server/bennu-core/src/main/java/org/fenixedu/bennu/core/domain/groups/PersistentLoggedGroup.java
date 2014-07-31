package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;

public class PersistentLoggedGroup extends PersistentLoggedGroup_Base {
    protected PersistentLoggedGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return LoggedGroup.get();
    }

    /**
     * Get or create singleton instance of {@link PersistentLoggedGroup}
     * 
     * @return singleton {@link PersistentLoggedGroup} instance
     */
    public static PersistentLoggedGroup getInstance() {
        return singleton(() -> find(PersistentLoggedGroup.class), () -> new PersistentLoggedGroup());
    }
}
