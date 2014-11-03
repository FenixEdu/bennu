package org.fenixedu.bennu.oauth.domain;

import org.fenixedu.bennu.core.domain.Bennu;

public class ExternalApplicationScope extends ExternalApplicationScope_Base {

    public ExternalApplicationScope() {
        super();
        setBennu(Bennu.getInstance());
    }

}
