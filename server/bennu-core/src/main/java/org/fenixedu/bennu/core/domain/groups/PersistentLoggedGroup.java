package org.fenixedu.bennu.core.domain.groups;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;

/**
 * @deprecated included for legacy de-serialization, not intended to be used directly anywhere
 */
@Deprecated
public class PersistentLoggedGroup extends PersistentLoggedGroup_Base {
    private PersistentLoggedGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return LoggedGroup.get();
    }
}
