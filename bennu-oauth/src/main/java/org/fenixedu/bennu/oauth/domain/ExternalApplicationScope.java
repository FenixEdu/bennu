/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth.
 *
 * Bennu OAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
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
