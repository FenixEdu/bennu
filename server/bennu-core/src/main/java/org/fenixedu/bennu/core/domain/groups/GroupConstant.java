package org.fenixedu.bennu.core.domain.groups;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;

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

    protected static <T extends GroupConstant> Stream<T> filter(Class<T> type) {
        return BennuGroupIndex.groupConstant(type);
    }
}
