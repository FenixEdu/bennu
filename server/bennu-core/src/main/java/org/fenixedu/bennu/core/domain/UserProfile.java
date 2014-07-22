package org.fenixedu.bennu.core.domain;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * User account profile information. This information includes names, email, avatar and preferred locale.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 */
public class UserProfile extends UserProfile_Base {
    protected UserProfile() {
        super();
    }

    public UserProfile(String givenNames, String familyNames, String displayName, String email, Locale preferredLocale) {
        this();
        changeName(givenNames, familyNames, displayName);
        setEmail(email);
        setPreferredLocale(preferredLocale);
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
        if (getGivenNames() != null) {
            builder.append(getGivenNames());
        }
        builder.append(" ");
        if (getFamilyNames() != null) {
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
    public void changeName(String given, String family, String display) {
        setGivenNames(cleanupName(given));
        setFamilyNames(cleanupName(family));
        setDisplayName(cleanupName(display));
        validateNames(getDisplayName(), getFullName());
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
    public void setEmail(String email) {
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
    public void setPreferredLocale(Locale preferredLocale) {
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

    /**
     * Sets the user's base avatar URL.
     * 
     * @see #getAvatarUrl()
     */
    @Override
    public void setAvatarUrl(String avatarUrl) {
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
    public void setLocalAvatar(Avatar localAvatar) {
        if (Objects.equal(getLocalAvatar(), localAvatar)) {
            return;
        }
        if (getLocalAvatar() != null) {
            getLocalAvatar().delete();
        }
        super.setLocalAvatar(localAvatar);
        super.setAvatarUrl(null);
    }

    private static void validateNames(String displayname, String fullname) {
        if (displayname == null) {
            return;
        }
        if (fullname == null) {
            throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
        }
        String[] fullnameparts = fullname.split(" ");
        String[] displayparts = displayname.split(" ");
        for (int n = 0, f = 0; n < displayparts.length; n++, f++) {
            if (fullnameparts.length == f) {
                throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
            }
            while (!displayparts[n].equalsIgnoreCase(fullnameparts[f])) {
                f++;
                if (fullnameparts.length == f) {
                    throw BennuCoreDomainException.displayNameNotContainedInFullName(displayname, fullname);
                }
            }
        }
    }

    private static String cleanupName(String name) {
        if (name == null) {
            return null;
        }
        return Strings.emptyToNull(name.trim().replaceAll("\\s+", " "));
    }
}