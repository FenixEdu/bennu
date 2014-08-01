package org.fenixedu.bennu.core.domain.groups;

import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;

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

    protected static <T extends GroupConstant> Optional<T> find(Class<T> type) {
        return filter(type).findAny();
    }
}
