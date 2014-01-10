package org.fenixedu.bennu.portal.domain;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ApplicationInfo extends FunctionalityInfo {

    private static final long serialVersionUID = 1L;

    private final Set<FunctionalityInfo> functionalities = new HashSet<>();

    public ApplicationInfo(String path, String group, Details details) {
        super(path, group, details);
    }

    public void addFunctionality(FunctionalityInfo functionalityInfo) {
        getFunctionalities().add(functionalityInfo);
    }

    @Override
    public JsonObject json() {
        JsonObject json = super.json();
        JsonArray array = new JsonArray();
        for (FunctionalityInfo functionality : getFunctionalities()) {
            array.add(functionality.json());
        }
        json.add("functionalities", array);
        return json;
    }

    public Set<FunctionalityInfo> getFunctionalities() {
        return functionalities;
    }
}
