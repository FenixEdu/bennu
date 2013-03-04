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
package pt.ist.bennu.core.domain;

import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.bennu.core.domain.groups.BennuGroup;
import pt.ist.bennu.core.domain.groups.UserGroup;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * The application end user.
 */
public class User extends User_Base {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

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

    private static UserPresentationStrategy strategy = defaultStrategy;

    public User(final String username) {
        super();
        setUsername(username);
        setBennu(Bennu.getInstance());
        setCreated(new DateTime());
    }

    /**
     * @see #usersGroups()
     */
    @Override
    public Set<UserGroup> getUserGroupSet() {
        throw new UnsupportedOperationException();
    }

    public Iterable<UserGroup> usersGroups() {
        return Iterables.filter(super.getUserGroupSet(), new Predicate<UserGroup>() {
            @Override
            public boolean apply(UserGroup group) {
                return group.getHost().equals(VirtualHost.getVirtualHostForThread());
            }
        });
    }

    public static User findByUsername(final String username) {
        for (final User user : Bennu.getInstance().getUsersSet()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public void generatePassword() {
        final String password = RandomStringUtils.randomAlphanumeric(10);
        setPassword(password);
    }

    @Override
    public void setPassword(final String password) {
        final String newHash = DigestUtils.sha512Hex(password);
        final String oldHash = getPassword();
        if (newHash.equals(oldHash)) {
            throw BennuCoreDomainException.badOldPassword();
        }
        super.setPassword(newHash);
    }

    public boolean matchesPassword(final String password) {
        final String hash = DigestUtils.sha512Hex(password);
        return hash.equals(getPassword());
    }

    public String getPresentationName() {
        return strategy.present(this);
    }

    public String getShortPresentationName() {
        return strategy.shortPresent(this);
    }

    @Override
    public Locale getPreferredLocale() {
        Locale locale = super.getPreferredLocale();
        if (locale != null) {
            return locale;
        }
        return Locale.getDefault();
    }

    public Set<BennuGroup> accessibleGroups() {
        return BennuGroup.userAccessibleGroups(this);
    }

    public static void registerUserPresentationStrategy(UserPresentationStrategy newStrategy) {
        if (strategy != defaultStrategy) {
            logger.warn("Overriding non-default strategy");
        }
        strategy = newStrategy;
    }

    public PasswordRecoveryRequest createNewPasswordRecoveryRequest() {
        super.setPassword(null);
        return new PasswordRecoveryRequest(this);
    }
}
