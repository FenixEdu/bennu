package org.fenixedu.bennu.core.domain.groups;

import java.util.Collection;
import java.util.Collections;

import pt.ist.fenixframework.dml.runtime.Relation;

@Deprecated
public abstract class GroupConstant extends GroupConstant_Base {
    protected GroupConstant() {
        super();
        setRootForGroupConstant(getRoot());
    }

    @Override
    protected Collection<Relation<?, ?>> getContextRelations() {
        return Collections.singleton(getRelationGroupConstantRoot());
    }
}
