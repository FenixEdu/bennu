package org.fenixedu.bennu.oauth.domain;

public enum ExternalApplicationState {

    ACTIVE("Active"),

    DELETED("Deleted"),

    BANNED("Banned");

    private String name;

    private ExternalApplicationState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}