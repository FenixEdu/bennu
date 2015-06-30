package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.GroupStrategy;

public final class PersistentGroupStrategy extends PersistentGroupStrategy_Base {
    private PersistentGroupStrategy(GroupStrategy strategy) {
        super();
        setStrategy(strategy);
    }

    @Override
    public Group toGroup() {
        return getStrategy();
    }

    public static PersistentGroupStrategy getInstance(final GroupStrategy strategy) {
        return singleton(
                () -> BennuGroupIndex.groupConstant(PersistentGroupStrategy.class)
                        .filter(group -> group.getStrategy().equals(strategy)).findAny(), () -> new PersistentGroupStrategy(
                        strategy));
    }
}
