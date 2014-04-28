package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

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
        PersistentLoggedGroup instance = BennuGroupIndex.getGroupConstant(PersistentLoggedGroup.class);
        return instance != null ? instance : create();
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentLoggedGroup create() {
        PersistentLoggedGroup instance = BennuGroupIndex.getGroupConstant(PersistentLoggedGroup.class);
        return instance != null ? instance : new PersistentLoggedGroup();
    }
}
