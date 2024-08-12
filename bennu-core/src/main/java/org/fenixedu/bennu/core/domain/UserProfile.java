package org.fenixedu.bennu.core.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.StringNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

/**
 * User account profile information. This information includes names, email, avatar and preferred locale.
 *
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 */
public class UserProfile extends UserProfile_Base {
    private static final Logger logger = LoggerFactory.getLogger(UserProfile.class);
    public static final String USER_NAME_CHANGED = "user.name.changed";

    protected UserProfile() {
        super();
        setBennu(Bennu.getInstance());
    }

    /**
     * New unlinked profile.
     *
     * @param givenNames person's given names
     * @param familyNames person's family names
     * @param displayName person's display name
     * @param email person's email address for communication
     * @param preferredLocale locale used to localize all content when the user is logged.
     */
    public UserProfile(final String givenNames, final String familyNames, final String displayName, final String email,
            final Locale preferredLocale) {
        this();
        changeName(givenNames, familyNames, displayName);
        setEmail(email);
        setPreferredLocale(preferredLocale);
    }

    @Atomic
    public void delete() {

        setBennu(null);
        if (getLocalAvatar() != null) {
            getLocalAvatar().delete();
        }
        getNameIndexSet().clear();
        setUser(null);

        deleteDomainObject();
    }

    @Override
    public User getUser() {
        // FIXME: remove when framework support read-only slots
        return super.getUser();
    }

    /**
     * A possibly shorter version of the full name. Used on most places to identify the person.
     *
     * @return a String with the display name or null.
     * @see #getFullName()
     */
    @Override
    public String getDisplayName() {
        return super.getDisplayName() != null ? super.getDisplayName() : getFullName();
    }

    /**
     * The given names of the person. First part of the full name.
     *
     * @return a String with the given names or null.
     * @see #getFamilyNames()
     * @see #getFullName()
     */
    @Override
    public String getGivenNames() {
        // FIXME: remove when framework support read-only slots
        return super.getGivenNames();
    }

    /**
     * The family names (surnames) of the person. Last part of the full name.
     *
     * @return a String with the family names or null.
     * @see #getFamilyNames()
     * @see #getFullName()
     */
    @Override
    public String getFamilyNames() {
        // FIXME: remove when framework support read-only slots
        return super.getFamilyNames();
    }

    /**
     * The full name of the person, composed of given and family names separated by a space.
     *
     * @return a String with the full name or null.
     * @see #getGivenNames()
     * @see #getFamilyNames()
     */
    public String getFullName() {
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(getGivenNames())) {
            builder.append(getGivenNames());
        }
        builder.append(" ");
        if (!Strings.isNullOrEmpty(getFamilyNames())) {
            builder.append(getFamilyNames());
        }
        return builder.toString().trim();
    }

    /**
     * Change the name by changing it's parts validating consistency between them. Namely ensures the display name is a subset of
     * the given and family names together.
     *
     * @param given person's given names
     * @param family person's family names
     * @param display person's display name
     */
    public void changeName(final String given, final String family, final String display) {
        setGivenNames(cleanupName(given));
        setFamilyNames(cleanupName(family));
        setDisplayName(cleanupName(display));
        validateNames(super.getDisplayName(), getFullName());
        NameIndex.updateNameIndex(this);
        Signal.emit(USER_NAME_CHANGED, this);
    }

    /**
     * User's primary email.
     *
     * @return a String with the user's email or null
     */
    @Override
    public String getEmail() {
        return super.getEmail();
    }

    /**
     * Change user's primary email.
     *
     * @param email a String with the user's primary email
     */
    @Override
    public void setEmail(final String email) {
        super.setEmail(email);
    }

    /**
     * The users's preferred locale for internationalized content. If this value is null the system localizes content based on
     * global defaults.
     *
     * @return a Locale instance or null
     */
    @Override
    public Locale getPreferredLocale() {
        return super.getPreferredLocale();
    }

    /**
     * Change the user's preferred locale.
     *
     * @param preferredLocale a Locale instance
     * @see #getPreferredLocale()
     */
    @Override
    public void setPreferredLocale(final Locale preferredLocale) {
        super.setPreferredLocale(preferredLocale);
    }

    /**
     * Returns this user's avatar URL or a mystery man avatar URL if not present. Avatar URLs can be parameterized with
     * {@code s=size}, by default size is 100.
     */
    @Override
    public String getAvatarUrl() {
        if (super.getAvatarUrl() != null) {
            return super.getAvatarUrl();
        }
        if (getLocalAvatar() != null) {
            return getLocalAvatar().url();
        }
        return Avatar.mysteryManUrl(this.getUser());
    }

    public String getFullAvatarUrl() {
        String avatarUrl = getAvatarUrl();
        if (StringUtils.isEmpty(avatarUrl)) {
            return null;
        }

        if (avatarUrl.startsWith("http")) {
            return avatarUrl;
        } else {
            return CoreConfiguration.getConfiguration().applicationUrl() + avatarUrl;
        }

    }

    /**
     * Sets the user's base avatar URL.
     *
     * @see #getAvatarUrl()
     */
    @Override
    public void setAvatarUrl(final String avatarUrl) {
        if (getLocalAvatar() != null) {
            getLocalAvatar().delete();
        }
        super.setAvatarUrl(avatarUrl);
    }

    /**
     * Sets this user's avatar to a locally stored avatar and updates the avatar url accordingly.
     *
     * @param localAvatar a Avatar instance with the image
     * @see #getAvatarUrl()
     */
    @Override
    public void setLocalAvatar(final Avatar localAvatar) {
        if (Objects.equal(getLocalAvatar(), localAvatar)) {
            return;
        }
        if (getLocalAvatar() != null) {
            getLocalAvatar().delete();
        }
        super.setLocalAvatar(localAvatar);
        super.setAvatarUrl(null);
    }

    /**
     * Search all profiles matching the given.
     *
     * @param name the query string
     * @param maxHits limit on the result size
     * @return all {@link UserProfile}s where the name contains all the terms of the query string
     */
    public static Stream<UserProfile> searchByName(final String name, final int maxHits) {
        if (logger.isTraceEnabled()) {
            long time = System.currentTimeMillis();
            Stream<UserProfile> matches = NameIndex.search(name, maxHits);
            logger.trace("Profile search for '{}' took {}ms", name, System.currentTimeMillis() - time);
            return matches;
        }
        return NameIndex.search(name, maxHits);
    }

    private static void validateNames(final String displayname, final String fullname) {
        if (displayname == null) {
            return;
        }
        if (fullname == null) {
            throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
        }
        List<String> fullnameParts =
                Arrays.asList(StringNormalizer.normalizeAndRemoveAccents(fullname).toLowerCase().trim().split("\\s+|-"));
        List<String> displaynameParts =
                Arrays.asList(StringNormalizer.normalizeAndRemoveAccents(displayname).toLowerCase().trim().split("\\s+|-"));
        if (!fullnameParts.containsAll(displaynameParts)) {
            throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
        }
    }

    private static String cleanupName(final String name) {
        if (name == null) {
            return null;
        }
        return Strings.emptyToNull(CharMatcher.whitespace().trimAndCollapseFrom(name, ' '));
    }
}
