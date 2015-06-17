package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
class PersistentAnonymousGroup extends PersistentAnonymousGroup_Base {
    private PersistentAnonymousGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return Group.anonymous();
    }
}
