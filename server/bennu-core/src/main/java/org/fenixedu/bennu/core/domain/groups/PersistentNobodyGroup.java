package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

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
        PersistentNobodyGroup instance = BennuGroupIndex.getGroupConstant(PersistentNobodyGroup.class);
        return instance != null ? instance : create();
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentNobodyGroup create() {
        PersistentNobodyGroup instance = BennuGroupIndex.getGroupConstant(PersistentNobodyGroup.class);
        return instance != null ? instance : new PersistentNobodyGroup();
    }
}
