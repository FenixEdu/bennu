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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.domain.groups.AnonymousGroup;
import org.fenixedu.bennu.core.domain.groups.AnyoneGroup;
import org.fenixedu.bennu.core.domain.groups.CompositionGroup;
import org.fenixedu.bennu.core.domain.groups.CustomGroup;
import org.fenixedu.bennu.core.domain.groups.DifferenceGroup;
import org.fenixedu.bennu.core.domain.groups.DynamicGroup;
import org.fenixedu.bennu.core.domain.groups.Group;
import org.fenixedu.bennu.core.domain.groups.IntersectionGroup;
import org.fenixedu.bennu.core.domain.groups.LoggedGroup;
import org.fenixedu.bennu.core.domain.groups.NegationGroup;
import org.fenixedu.bennu.core.domain.groups.NobodyGroup;
import org.fenixedu.bennu.core.domain.groups.UnionGroup;
import org.fenixedu.bennu.core.domain.groups.UserGroup;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
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
        final String password = RandomStringUtils.randomAlphanumeric(10);
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

    public Set<Group> accessibleGroups() {
        long start = System.currentTimeMillis();
        Set<Group> groups = new HashSet<>();
        Set<Group> ignored = new HashSet<>();
        for (UserGroup group : getUserGroupSet()) {
            processAccessibleGroups(groups, ignored, group);
        }
        for (Group group : CustomGroup.groupsForUser(this)) {
            processAccessibleGroups(groups, ignored, group);
        }
        for (NegationGroup group : Bennu.getInstance().getNegationSet()) {
            if (group.isMember(this)) {
                processAccessibleGroups(groups, ignored, group);
            }
        }
        logger.debug("Accessible groups processing for user {} took {}", getUsername(), new Period(System.currentTimeMillis()
                - start).toString());
        return groups;
    }

    // These we are not interested to see listed as accessible groups, either because they are obvious, or because they are meaningless
    // unless used in some context.
    private static Set<Class<? extends Group>> IGNORES = new HashSet<>(Arrays.asList(AnonymousGroup.class, AnyoneGroup.class,
            NobodyGroup.class, LoggedGroup.class, DifferenceGroup.class, IntersectionGroup.class, NegationGroup.class,
            UnionGroup.class, UserGroup.class));

    private void processAccessibleGroups(Set<Group> groups, Set<Group> ignored, Group group) {
        if (!groups.contains(group) && !ignored.contains(group)) {
            if (IGNORES.contains(group.getClass())) {
                ignored.add(group);
            } else {
                groups.add(group);
            }
            for (DynamicGroup dynamic : DynamicGroup.dynamicGroupsContainingGroup(group)) {
                processAccessibleGroups(groups, ignored, dynamic);
            }
            for (CompositionGroup composition : CompositionGroup.compositionsContainingGroup(group)) {
                if (composition instanceof UnionGroup || composition.isMember(this)) {
                    processAccessibleGroups(groups, ignored, composition);
                }
            }
        }
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
