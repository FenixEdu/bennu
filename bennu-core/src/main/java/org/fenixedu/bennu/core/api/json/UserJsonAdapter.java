package org.fenixedu.bennu.core.api.json;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.Avatar;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.joda.time.format.ISODateTimeFormat;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(User.class)
public class UserJsonAdapter implements JsonAdapter<User> {
    @Override
    public JsonElement view(User user, JsonBuilder ctx) {
        JsonObject jsonObject = new JsonObject();
        UserProfile profile = user.getProfile();
        jsonObject.addProperty("id", user.getExternalId());
        jsonObject.addProperty("username", user.getUsername());
        jsonObject.addProperty("name", profile.getFullName());
        jsonObject.addProperty("displayName", user.getDisplayName());
        jsonObject.addProperty("givenNames", profile.getGivenNames());
        jsonObject.addProperty("familyNames", profile.getFamilyNames());
        JsonUtils.put(jsonObject, "email", user.getEmail());
        jsonObject.addProperty("active", !user.isLoginExpired());
        user.getExpiration().ifPresent(e -> jsonObject.addProperty("expiration", ISODateTimeFormat.date().print(e)));
        JsonUtils.put(jsonObject, "avatar", profile.getAvatarUrl());
        Optional.ofNullable(profile.getPreferredLocale()).ifPresent(preferredLocale -> jsonObject.add("preferredLocale", ctx.view(preferredLocale)));
        Optional.ofNullable(profile.getEmailLocale()).ifPresent(emailLocale -> jsonObject.add("emailLocale", ctx.view(emailLocale)));

        return jsonObject;
    }

    @Override
    public User create(JsonElement json, JsonBuilder ctx) {
        JsonObject object = json.getAsJsonObject();
        User user = new User(parseAndUpdateOrCreateProfile(object, ctx, null));
        changePassword(user, object);
        changeAvatar(user.getProfile(), object);
        changeExpiration(user, object);
        return user;
    }

    @Override
    public User update(JsonElement json, User obj, JsonBuilder ctx) {
        JsonObject object = json.getAsJsonObject();
        User user;
        if (object.has("id")) {
            user = FenixFramework.getDomainObject(object.get("id").getAsString());
        } else if (object.has("username")) {
            user = User.findByUsername(object.get("username").getAsString());
            if (user == null) {
                return null;
            }
        } else {
            return null;
        }
        parseAndUpdateOrCreateProfile(object, ctx, user.getProfile());
        changePassword(user, object);
        changeAvatar(user.getProfile(), object);
        changeExpiration(user, object);
        return user;
    }

    private void changeExpiration(User user, JsonObject json) {
        String expiration = JsonUtils.getString(json, "expiration");

        if (Strings.isNullOrEmpty(expiration)) {
            user.openLoginPeriod();
        } else {
            user.closeLoginPeriod(ISODateTimeFormat.date().parseLocalDate(expiration));
        }
    }

    private UserProfile parseAndUpdateOrCreateProfile(JsonObject json, JsonBuilder ctx, UserProfile current) {
        String given = JsonUtils.getString(json, "givenNames");
        String family = JsonUtils.getString(json, "familyNames");
        String display = JsonUtils.getString(json, "displayName");
        String email = JsonUtils.getString(json, "email");
        Locale preferredLocale = JsonUtils.get(json, "preferredLocale", ctx, Locale.class);
        Locale emailLocale = JsonUtils.get(json, "emailLocale", ctx, Locale.class);

        if (current != null) {
            current.changeName(given, family, display);
            current.setEmail(email);
            current.setPreferredLocale(preferredLocale);
            current.setEmailLocale(emailLocale);
            changeAvatar(current, json);
            return current;
        }
        UserProfile userProfile = new UserProfile(given, family, display, email, preferredLocale);
        userProfile.setEmailLocale(emailLocale);
        return userProfile;
    }

    private void changePassword(User user, JsonObject json) {
        String password = JsonUtils.getString(json, "password");
        if (Strings.isNullOrEmpty(password)) {
            // Don't change the password.
            return;
        }
        String passwordCheck = JsonUtils.getString(json, "passwordCheck");
        if (!Objects.equals(password, passwordCheck)) {
            throw BennuCoreDomainException.passwordCheckDoesNotMatch();
        }
        //avoid same password
        if (user.matchesPassword(password)) {
            throw BennuCoreDomainException.passwordIsTheSame();
        }

        user.changePassword(password);
    }

    private void changeAvatar(UserProfile profile, JsonObject json) {
        if (json.has("avatar")) {
            if (json.get("avatar").isJsonPrimitive()) {
                profile.setAvatarUrl(json.get("avatar").getAsString());
            } else {
                JsonObject obj = json.get("avatar").getAsJsonObject();
                byte[] img = BaseEncoding.base64().decode(JsonUtils.getString(obj, "img"));
                String mimeType = JsonUtils.getString(obj, "mimeType");
                if (obj.has("x1")) {
                    int x1 = JsonUtils.getInt(obj, "x1");
                    int y1 = JsonUtils.getInt(obj, "y1");
                    int x2 = JsonUtils.getInt(obj, "x2");
                    int y2 = JsonUtils.getInt(obj, "y2");
                    profile.setLocalAvatar(Avatar.crop(img, mimeType, x1, y1, x2, y2));
                } else {
                    profile.setLocalAvatar(Avatar.create(img, mimeType));
                }
            }
        } else {
            profile.setAvatarUrl(null);
        }
    }
}
