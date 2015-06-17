package org.fenixedu.bennu.core.domain.groups;

import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;

@Deprecated
public abstract class GroupConstant extends GroupConstant_Base {
    protected GroupConstant() {
        super();
        setRootForGroupConstant(getRoot());
    }

    @Override
    protected boolean isGarbageCollectable() {
        // Singleton group, no point in delete
        return false;
    }

    protected static <T extends GroupConstant> Stream<T> filter(Class<T> type) {
        return BennuGroupIndex.groupConstant(type);
    }
}
