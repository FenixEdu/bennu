package org.fenixedu.bennu.oauth.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

import com.google.common.base.Strings;

public class ExternalApplicationScope extends ExternalApplicationScope_Base {

    public ExternalApplicationScope() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Override
    public void setScopeKey(String scopeKey) {

        if (Strings.isNullOrEmpty(scopeKey)) {
            throw BennuCoreDomainException.cannotCreateEntity();
        }

        Optional<ExternalApplicationScope> scopeForKey = forKey(scopeKey);

        if (scopeForKey.isPresent() && !scopeForKey.get().equals(this)) {
            throw BennuCoreDomainException.cannotCreateEntity();
        }

        super.setScopeKey(scopeKey);
    }

    public static Optional<ExternalApplicationScope> forKey(String... key) {
        List<String> keys = Arrays.asList(key);
        return Bennu.getInstance().getScopesSet().stream().filter(scope -> keys.contains(scope.getScopeKey())).findAny();
    }

}
