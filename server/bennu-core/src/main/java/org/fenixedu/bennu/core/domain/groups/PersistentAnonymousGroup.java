package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.Group;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
public class PersistentAnonymousGroup extends PersistentAnonymousGroup_Base {
    private PersistentAnonymousGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return AnonymousGroup.get();
    }
}
