package org.fenixedu.bennu.core.json.adapters;

import java.util.Locale;
import java.util.Objects;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonUtils;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(User.class)
public class UserJsonAdapter implements JsonAdapter<User> {
    @Override
    public JsonElement view(User user, JsonBuilder ctx) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getExternalId());
        jsonObject.addProperty("username", user.getUsername());
        jsonObject.addProperty("name", user.getPresentationName());
        JsonUtils.put(jsonObject, "givenNames", user.getProfile().getGivenNames());
        JsonUtils.put(jsonObject, "familyNames", user.getProfile().getFamilyNames());
        JsonUtils.put(jsonObject, "displayName", user.getProfile().getDisplayName());
        JsonUtils.put(jsonObject, "email", user.getProfile().getEmail());
        JsonUtils.put(jsonObject, "preferredLocale", ctx.view(user.getProfile().getPreferredLocale()));
        return jsonObject;
    }

    @Override
    public User create(JsonElement json, JsonBuilder ctx) {
        JsonObject object = json.getAsJsonObject();
        User user = new User(parseAndUpdateOrCreateProfile(object, ctx, null));
        changePassword(user, object);
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
        } else {
            return null;
        }
        parseAndUpdateOrCreateProfile(object, ctx, user.getProfile());
        changePassword(user, object);
        return user;
    }

    private UserProfile parseAndUpdateOrCreateProfile(JsonObject json, JsonBuilder ctx, UserProfile current) {
        String given = JsonUtils.getString(json, "givenNames");
        String family = JsonUtils.getString(json, "familyNames");
        String display = JsonUtils.getString(json, "displayName");
        String email = JsonUtils.getString(json, "email");
        Locale preferredLocale = JsonUtils.get(json, "preferredLocale", ctx, Locale.class);
        if (current != null) {
            current.changeName(given, family, display);
            current.setEmail(email);
            current.setPreferredLocale(preferredLocale);
            return current;
        }
        return new UserProfile(given, family, display, email, preferredLocale);
    }

    private void changePassword(User user, JsonObject json) {
        String password = JsonUtils.getString(json, "password");
        String passwordCheck = JsonUtils.getString(json, "passwordCheck");
        if (!Objects.equals(password, passwordCheck)) {
            throw BennuCoreDomainException.passwordCheckDoesNotMatch();
        }
        user.changePassword(password);
    }
}
