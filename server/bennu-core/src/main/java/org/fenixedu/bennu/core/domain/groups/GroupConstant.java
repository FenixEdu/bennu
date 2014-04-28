package org.fenixedu.bennu.core.domain.groups;

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
}
