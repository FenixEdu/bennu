package org.fenixedu.bennu.oauth.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.fenixedu.bennu.core.domain.Bennu;

public class ExternalApplicationScope extends ExternalApplicationScope_Base {

    public ExternalApplicationScope() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static Optional<ExternalApplicationScope> forKey(String[] key) {
        List<String> keys = Arrays.asList(key);
        return Bennu.getInstance().getScopesSet().stream().filter(scope -> keys.contains(scope.getScopeKey())).findAny();
    }

}
