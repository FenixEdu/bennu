package org.fenixedu.bennu.core.json;

public class Contact {

    private ContactType type;
    private String value;

    public Contact(ContactType type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
