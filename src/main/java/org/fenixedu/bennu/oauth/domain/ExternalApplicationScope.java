package org.fenixedu.bennu.oauth.domain;

import java.util.Optional;

import org.fenixedu.bennu.core.domain.Bennu;

public class ExternalApplicationScope extends ExternalApplicationScope_Base {

    public ExternalApplicationScope() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static Optional<ExternalApplicationScope> forKey(String key) {
        return Bennu.getInstance().getScopesSet().stream().filter(scope -> key.equals(scope.getScopeKey())).findAny();
    }

}
