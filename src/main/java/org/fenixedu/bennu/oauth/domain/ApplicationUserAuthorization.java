package org.fenixedu.bennu.oauth.domain;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ApplicationUserAuthorization extends ApplicationUserAuthorization_Base {

    public ApplicationUserAuthorization(User user, ExternalApplication application) {
        super();
        setUser(user);
        setApplication(application);
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        Set<ApplicationUserSession> sessions = new HashSet<ApplicationUserSession>(getSessionSet());
        for (ApplicationUserSession session : sessions) {
            session.delete();
        }
        setUser(null);
        setApplication(null);
        deleteDomainObject();
    }

}