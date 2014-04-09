package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

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
        PersistentAnonymousGroup instance = BennuGroupIndex.getGroupConstant(PersistentAnonymousGroup.class);
        return instance != null ? instance : create();
    }

    @Atomic(mode = TxMode.WRITE)
    private static PersistentAnonymousGroup create() {
        PersistentAnonymousGroup instance = BennuGroupIndex.getGroupConstant(PersistentAnonymousGroup.class);
        return instance != null ? instance : new PersistentAnonymousGroup();
    }
}
