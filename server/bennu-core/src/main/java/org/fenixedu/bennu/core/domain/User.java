/*
 * User.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.domain;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.UUID;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/**
 * The application end user.
 */
public final class User extends User_Base implements Principal {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private static SecureRandom prng = null;

    static {
        try {
            prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Please provide SHA1PRNG implementation", e);
        }
    }

    public interface UserPresentationStrategy {
        public String present(User user);

        public String shortPresent(User user);
    }

    private static final UserPresentationStrategy defaultStrategy = new UserPresentationStrategy() {

        @Override
        public String present(User user) {
            return user.getUsername();
        }

        @Override
        public String shortPresent(User user) {
            return user.getUsername();
        }

    };

    public static final Comparator<User> COMPARATOR_BY_NAME = new Comparator<User>() {

        @Override
        public int compare(final User user1, final User user2) {
            return user1.getUsername().compareTo(user2.getUsername());
        }

    };

    public static class UserToUsername implements Function<User, String> {
        @Override
        public String apply(User user) {
            return user.getUsername();
        }
    }

    public static class UsernameToUser implements Function<String, User> {
        @Override
        public User apply(String username) {
            return User.findByUsername(username);
        }
    }

    private static UserPresentationStrategy strategy = defaultStrategy;

    public User(final String username) {
        super();
        if (findByUsername(username) != null) {
            throw BennuCoreDomainException.duplicateUsername(username);
        }
        setBennu(Bennu.getInstance());
        setCreated(new DateTime());
        setUsername(username);
    }

    @Override
    public String getUsername() {
        //FIXME: remove when the framework enables read-only slots
        return super.getUsername();
    }

    @Override
    public DateTime getCreated() {
        //FIXME: remove when the framework enables read-only slots
        return super.getCreated();
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public String generatePassword() {
        final String password = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        changePassword(password);
        return password;
    }

    public void changePassword(final String password) {
        if (getPassword() != null) {
            String newHashWithOldSalt = Hashing.sha512().hashString(getSalt() + password, Charsets.UTF_8).toString();
            if (newHashWithOldSalt.equals(getPassword())) {
                throw BennuCoreDomainException.badOldPassword();
            }
        }
        byte salt[] = new byte[64];
        prng.nextBytes(salt);
        setSalt(BaseEncoding.base64().encode(salt));
        String hash = Hashing.sha512().hashString(getSalt() + password, Charsets.UTF_8).toString();
        setPassword(hash);
    }

    public boolean isLoginExpired() {
        return getExpiration() != null && new LocalDate().isAfter(getExpiration());
    }

    public boolean matchesPassword(final String password) {
        if (getPassword() == null) {
            return true;
        }
        final String hash = Hashing.sha512().hashString(getSalt() + password, Charsets.UTF_8).toString();
        return hash.equals(getPassword());
    }

    public String getPresentationName() {
        return strategy.present(this);
    }

    public String getShortPresentationName() {
        return strategy.shortPresent(this);
    }

    public static User findByUsername(final String username) {
        for (final User user : Bennu.getInstance().getUserSet()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void registerUserPresentationStrategy(UserPresentationStrategy newStrategy) {
        if (strategy != defaultStrategy) {
            logger.warn("Overriding non-default strategy");
        }
        strategy = newStrategy;
    }
}
