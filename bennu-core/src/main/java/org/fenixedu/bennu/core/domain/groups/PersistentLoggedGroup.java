package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
class PersistentLoggedGroup extends PersistentLoggedGroup_Base {
    private PersistentLoggedGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return Group.logged();
    }
}
