package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.core.domain.User;

public class PersistentAlertMessageUserViewCount extends PersistentAlertMessageUserViewCount_Base {
    
    public PersistentAlertMessageUserViewCount(final PersistentAlertMessage message, final User user) {
        setPersistentAlertMessage(message);
        setUser(user);
    }

    public void delete() {
        setUser(null);
        setPersistentAlertMessage(null);
        deleteDomainObject();
    }

}
