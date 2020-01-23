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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

public class ExternalApplication extends ExternalApplication_Base {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApplication.class);

    public ExternalApplication() {
        super();
        init();
    }

    protected void init() {
        setBennu(Bennu.getInstance());
        setSecret(Base64.getEncoder().encodeToString(
                Hashing.sha512().hashBytes(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)).asBytes()));
        setState(ExternalApplicationState.ACTIVE);
    }

    public void setScopeList(List<ExternalApplicationScope> newScopes) {
        Set<ExternalApplicationScope> oldScopes = getScopesSet();

        Set<ExternalApplicationScope> result = Sets.difference(Sets.newHashSet(newScopes), Sets.newHashSet(oldScopes));

        if (result.size() > 0) {
            deleteAuthorizations();
        }
        oldScopes.clear();
        oldScopes.addAll(newScopes);
    }

    public void removeScope(ExternalApplicationScope scope) {
        List<ExternalApplicationScope> oldScopes = getScopeList();
        if (oldScopes.contains(scope)) {
            oldScopes.remove(scope);
            setScopeList(oldScopes);
        }
    }

    public List<ExternalApplicationScope> getScopeList() {
        return new ArrayList<ExternalApplicationScope>(getScopesSet());
    }

    public boolean matchesUrl(String redirectUrl) {
        return !Strings.isNullOrEmpty(redirectUrl) &&
                 Arrays.asList(getRedirectUrl().split("\\|")).contains(redirectUrl);
    }

    public boolean matchesSecret(String secret) {
        return !Strings.isNullOrEmpty(secret) && secret.equals(getSecret());
    }

    public boolean matches(String redirectUrl, String secret) {
        return matchesUrl(redirectUrl) && matchesSecret(secret);
    }

    public ApplicationUserSession getApplicationUserSession(String code) {
        String codeDecoded = code;
        try {
            codeDecoded = URLDecoder.decode(code, StandardCharsets.UTF_8.name());
            String userId = OAuthUtils.getUserIdFromCode(codeDecoded, getCodeSecret());
            User user = User.findByUsername(userId);

            Optional<ApplicationUserAuthorization> authOptional = user.getApplicationUserAuthorizationSet().stream().filter(s -> s.getApplication().getOid().equals(getOid())).findFirst();
            if (authOptional.isPresent()) {
                final String finalCode = codeDecoded;
                ApplicationUserAuthorization auth = authOptional.get();
                Optional<ApplicationUserSession> session = auth.getSessionSet().stream().filter(s -> s.matchesCode(finalCode)).findFirst();
                if (session.isPresent()) {
                    return session.get();
                }
            }
        } catch (UnsupportedEncodingException | RuntimeException e) {
            for (ApplicationUserSession applicationUserSession : getApplicationUserSessionSet()) {
                if (applicationUserSession.matchesCode(codeDecoded)) {
                    return applicationUserSession;
                }
            }
        }
        return null;
    }

    public void deleteAuthorizations() {
        for (ApplicationUserAuthorization authorization : new HashSet<ApplicationUserAuthorization>(
                getApplicationUserAuthorizationSet())) {
            authorization.delete();
        }
    }

    private Set<ApplicationUserSession> getApplicationUserSessionSet() {
        return FluentIterable.from(getApplicationUserAuthorizationSet())
                .transformAndConcat(new Function<ApplicationUserAuthorization, Iterable<ApplicationUserSession>>() {
                    @Override
                    public Iterable<ApplicationUserSession> apply(ApplicationUserAuthorization auth) {
                        return auth.getSessionSet();
                    }
                }).toSet();
    }

    public InputStream getLogoStream() {
        return null;
    }

    public String getAuthorApplicationName() {
        String name = getAuthorName();
        if (!Strings.isNullOrEmpty(name)) {
            return name;
        } else {
            return getAuthor().getDisplayName();
        }
    }

    public void setLogoStream(InputStream stream) {
        try {
            if (stream != null) {
                byte[] byteArray = ByteStreams.toByteArray(stream);
                if (byteArray.length > 0) {
                    setLogo(byteArray);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Optional<ApplicationUserAuthorization> getApplicationUserAuthorization(User user) {
        return getApplicationUserAuthorizationSet().stream().filter(auth -> auth.getUser().equals(user)).findAny();
    }

    @Override
    public ExternalApplicationState getState() {
        final ExternalApplicationState state = super.getState();
        if (state == null) {
            setState(ExternalApplicationState.ACTIVE);
        }
        return super.getState();
    }

    public boolean hasApplicationUserAuthorization(User user) {
        return getApplicationUserAuthorization(user).isPresent();
    }

    public boolean isActive() {
        return getState().equals(ExternalApplicationState.ACTIVE);
    }

    public boolean isBanned() {
        return getState().equals(ExternalApplicationState.BANNED);
    }

    public boolean isDeleted() {
        return getState().equals(ExternalApplicationState.DELETED);
    }

    public boolean isEditable() {
        return isActive() || isBanned();
    }

    @Override
    @Atomic
    public void setState(ExternalApplicationState state) {
        super.setState(state);
    }

    public void setActive() {
        setState(ExternalApplicationState.ACTIVE);
    }

    public void setBanned() {
        setState(ExternalApplicationState.BANNED);
    }

    public void setDeleted() {
        setState(ExternalApplicationState.DELETED);
    }

    @Override
    public String getSecret() {
        return super.getSecret();
    }
}
