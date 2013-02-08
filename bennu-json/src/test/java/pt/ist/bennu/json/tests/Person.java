package pt.ist.bennu.json.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Person {

    protected String name;
    protected List<Contact> contacts;

    public Person(String name) {
        super();
        this.name = name;
        this.contacts = new ArrayList<>();
    }

    public void addContact(ContactType type, String value) {
        contacts.add(new Contact(type, value));
    }

    public Collection<Contact> getContacts() {
        return contacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", Person.class.getSimpleName(), name);
    }

}
