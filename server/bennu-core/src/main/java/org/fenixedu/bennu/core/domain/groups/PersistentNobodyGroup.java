package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;

public class PersistentNobodyGroup extends PersistentNobodyGroup_Base {
    protected PersistentNobodyGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return NobodyGroup.get();
    }

    /**
     * Get or create singleton instance of {@link PersistentNobodyGroup}
     * 
     * @return singleton {@link PersistentNobodyGroup} instance
     */
    public static PersistentNobodyGroup getInstance() {
        return singleton(() -> find(PersistentNobodyGroup.class), () -> new PersistentNobodyGroup());
    }
}
