package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.Group;

public class PersistentAnonymousGroup extends PersistentAnonymousGroup_Base {
    protected PersistentAnonymousGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return AnonymousGroup.get();
    }

    /**
     * Get or create singleton instance of {@link PersistentAnonymousGroup}
     * 
     * @return singleton {@link PersistentAnonymousGroup} instance
     */
    public static PersistentAnonymousGroup getInstance() {
        return singleton(() -> find(PersistentAnonymousGroup.class), () -> new PersistentAnonymousGroup());
    }
}
