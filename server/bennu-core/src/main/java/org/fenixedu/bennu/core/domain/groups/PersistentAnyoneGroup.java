package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

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
        PersistentAnyoneGroup instance = BennuGroupIndex.getGroupConstant(PersistentAnyoneGroup.class);
        return instance != null ? instance : create();
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentAnyoneGroup create() {
        PersistentAnyoneGroup instance = BennuGroupIndex.getGroupConstant(PersistentAnyoneGroup.class);
        return instance != null ? instance : new PersistentAnyoneGroup();
    }
}
