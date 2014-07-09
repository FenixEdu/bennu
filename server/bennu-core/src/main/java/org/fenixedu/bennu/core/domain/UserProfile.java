package org.fenixedu.bennu.core.domain;

import java.util.Locale;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

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
    public String getDisplayName() {
        return super.getDisplayName() != null ? super.getDisplayName() : getFullName();
    }

    @Override
    public String getGivenNames() {
        // FIXME: remove when framework support read-only slots
        return super.getGivenNames();
    }

    @Override
    public String getFamilyNames() {
        // FIXME: remove when framework support read-only slots
        return super.getFamilyNames();
    }

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

    public void changeName(String given, String family, String display) {
        setGivenNames(cleanupName(given));
        setFamilyNames(cleanupName(family));
        setDisplayName(cleanupName(display));
        validateNames(getDisplayName(), getFullName());
    }

    /**
     * Returns this user's avatar URL, never null. Avatar URLs are templates with the variable {size} in it, that must be replaced
     * with the desired size by the invoking client.
     */
    @Override
    public String getAvatarUrl() {
        if (super.getAvatarUrl() != null) {
            return super.getAvatarUrl();
        }
        return Avatar.mysteryManUrl(this.getUser());
    }

    /**
     * Sets the user's avatar URL template.
     * 
     * @see UserProfile#getAvatarUrl()
     */
    @Override
    public void setAvatarUrl(String avatarUrl) {
        if (getLocalAvatar() != null) {
            getLocalAvatar().delete();
        }
        super.setAvatarUrl(avatarUrl);
    }

    /**
     * @param localAvatar
     * @see org.fenixedu.bennu.core.domain.UserProfile_Base#setLocalAvatar(org.fenixedu.bennu.core.domain.Avatar)
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
        super.setAvatarUrl(localAvatar.urlTemplate());
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
