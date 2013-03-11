package pt.ist.bennu.dispatch.model;

import java.io.Serializable;

import pt.ist.bennu.core.util.MultiLanguageString;

import com.google.gson.JsonObject;

public class FunctionalityInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String path;

    private final String group;

    private final Details details;

    public FunctionalityInfo(String path, String group, Details details) {
        this.path = path;
        this.group = group;
        this.details = details;
    }

    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    public MultiLanguageString getTitle() {
        return details.getTitle();
    }

    public MultiLanguageString getDescription() {
        return details.getDescription();
    }

    public JsonObject json() {
        JsonObject json = new JsonObject();
        json.add("title", getTitle().json());
        json.add("description", getDescription().json());
        json.addProperty("path", path);
        json.addProperty("accessExpression", group);
        return json;
    }
}
