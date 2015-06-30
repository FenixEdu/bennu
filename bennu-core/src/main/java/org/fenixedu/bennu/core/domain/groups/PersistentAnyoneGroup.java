package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
class PersistentAnyoneGroup extends PersistentAnyoneGroup_Base {
    private PersistentAnyoneGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return Group.anyone();
    }
}
