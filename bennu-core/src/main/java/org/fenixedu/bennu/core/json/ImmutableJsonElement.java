package org.fenixedu.bennu.core.json;

import com.google.gson.JsonElement;

public class ImmutableJsonElement<J extends JsonElement> {

    private J json;

    public ImmutableJsonElement(final J json) {
        this.json = json;
    }

    public J get() {
        return (J) json.deepCopy();
    }

    @Override
    public String toString() {
        return json == null ? null : json.toString();
    }

    public static <J extends JsonElement> ImmutableJsonElement<J> parse(final String string) {
        return of((J) JsonUtils.parseJsonElement(string));
    }

    public static <J extends JsonElement> ImmutableJsonElement<J> of(final J json) {
        return new ImmutableJsonElement<J>(json);
    }

}
