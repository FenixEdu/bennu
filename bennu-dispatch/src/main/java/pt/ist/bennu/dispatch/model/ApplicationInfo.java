package pt.ist.bennu.dispatch.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ApplicationInfo extends FunctionalityInfo {

    private final Set<FunctionalityInfo> functionalities = new HashSet<>();

    public ApplicationInfo(String bundle, String title, String description, String path, String group) {
        super(bundle, title, description, path, group);
    }

    public void addFunctionality(FunctionalityInfo functionalityInfo) {
        functionalities.add(functionalityInfo);
    }

    @Override
    public JsonObject json() {
        JsonObject json = super.json();
        JsonArray array = new JsonArray();
        for (FunctionalityInfo functionality : functionalities) {
            array.add(functionality.json());
        }
        json.add("functionalities", array);
        return json;
    }
}
