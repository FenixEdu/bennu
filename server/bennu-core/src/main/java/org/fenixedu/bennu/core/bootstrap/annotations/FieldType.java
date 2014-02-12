package org.fenixedu.bennu.core.bootstrap.annotations;

/**
 * Defines the type of field to be rendered for a given {@link Field}.
 */
public enum FieldType {
    TEXT("text"), PASSWORD("password"), EMAIL("email"), SELECT_ONE("selectOne");

    private final String fieldType;

    private FieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldType() {
        return fieldType;
    }
}
