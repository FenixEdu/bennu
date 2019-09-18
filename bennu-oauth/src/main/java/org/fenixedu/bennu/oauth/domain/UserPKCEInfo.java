package org.fenixedu.bennu.oauth.domain;

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class UserPKCEInfo extends UserPKCEInfo_Base {
    
    public UserPKCEInfo(User user, ExternalApplication application, String codeChanllenge) {
        super();
        setUser(user);
        setApplication(application);
        setCodeChanllenge(codeChanllenge);
    }
    
    @Atomic(mode = TxMode.WRITE)
    public void delete() {
    	setUser(null);
    	setSession(null);
    	setApplication(null);
        deleteDomainObject();
    }
    
}
