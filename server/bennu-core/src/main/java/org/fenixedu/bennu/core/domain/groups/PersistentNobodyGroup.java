package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
class PersistentNobodyGroup extends PersistentNobodyGroup_Base {
    private PersistentNobodyGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return Group.nobody();
    }
}
