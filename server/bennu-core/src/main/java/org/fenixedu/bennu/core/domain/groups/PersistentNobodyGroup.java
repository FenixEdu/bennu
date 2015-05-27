package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
public class PersistentNobodyGroup extends PersistentNobodyGroup_Base {
    private PersistentNobodyGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return NobodyGroup.get();
    }
}
