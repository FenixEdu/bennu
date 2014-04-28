package org.fenixedu.bennu.core.json;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(JUnit4.class)
public class TestJsonSerialization {

    private static User user;
    private static JsonBuilder jsonRegistry;
    private static JsonParser parser;

    private static JsonObject parse(String jsonData) {
        return (JsonObject) parser.parse(jsonData);
    }

    @BeforeClass
    public static void cenas() {
        user = new User("test", "testpwd", "1");
        jsonRegistry = new JsonBuilder();
        jsonRegistry.setDefault(User.class, UserAdapter.class);
        parser = new JsonParser();
    }

    @Test
    public void testSerialization() {
        String expected = "{\"name\":\"test\",\"password\":\"testpwd\",\"number\":\"1\"}";
        Assert.assertEquals(expected, jsonRegistry.view(user).toString());
    }

    @Test
    public void testCreation() {
        String jsonString = "{\"name\":\"test_creation\",\"password\":\"pass_creation\",\"number\":\"2\"}";
        final User userCreated = jsonRegistry.create(parse(jsonString), User.class);
        Assert.assertEquals("test_creation", userCreated.getName());
        Assert.assertEquals("pass_creation", userCreated.getPassword());
        Assert.assertEquals("2", userCreated.getNumber());
    }

    @Test
    public void testUpdate() {
        String jsonString = "{\"name\":\"test_update\",\"password\":\"pass_update\",\"number\":\"3\"}";
        jsonRegistry.update(parse(jsonString), user);
        Assert.assertEquals("test_update", user.getName());
        Assert.assertEquals("pass_update", user.getPassword());
        Assert.assertEquals("3", user.getNumber());
    }

    @Test
    public void testCustomSerialization() {
        String expected = "{\"name\":\"test\",\"cenas\":\"isto sao cenas\"}";
        Assert.assertEquals(expected, jsonRegistry.view(user, UserSpecificViewer.class).toString());
    }

    @Test
    public void testCustomCreation() {
        String jsonData = "{\"name\":\"test_custom_creation\",\"password\":\"pass_custom_creation\",\"number\":\"4\"}";
        final User createdUser = jsonRegistry.create(parse(jsonData), User.class, UserSpecificCreator.class);
        Assert.assertEquals("test_custom_creation", createdUser.getName());
        Assert.assertEquals("cGFzc19jdXN0b21fY3JlYXRpb24=", createdUser.getPassword());
        Assert.assertEquals("4", createdUser.getNumber());
    }

    @Test
    public void testSetDefaultViewer() {
        String expected = "{\"name\":\"test\",\"cenas\":\"isto sao cenas\"}";
        jsonRegistry.setDefault(User.class, UserSpecificViewer.class);
        Assert.assertEquals(expected, jsonRegistry.view(user).toString());
    }

    @Test
    public void testPersonViewerOnUser() {
        JsonBuilder reg = new JsonBuilder();
        reg.setDefault(Person.class, PersonViewer.class);
        //reg.setDefault(User.class, UserAdapter.class);
        User thisUser = new User("test", "testpwd", "1");
        String expected = "{\"name\":\"test\"}";
        Assert.assertEquals(expected, reg.view(thisUser).toString());
    }

    @Test
    public void testParse() {
        String expected =
                "{\"name\":\"john\",\"contacts\":[{\"type\":\"EMAIL\",\"value\":\"bla@gmail.com\"},{\"type\":\"MOBILE_PHONE\",\"value\":\"555241541\"}]}";
        String result =
                "{\"name\":\"john\",\"contacts\":[{\"value\":\"555241541\",\"type\":\"MOBILE_PHONE\"},{\"value\":\"bla@gmail.com\",\"type\":\"EMAIL\"}]}";
        JsonParser p = new JsonParser();
        final JsonElement eJson = p.parse(expected);
        final JsonElement pJson = p.parse(result);
        Assert.assertNotEquals(eJson, pJson);
    }

    @Test
    public void testPersonViewerWithContacts() {
        JsonBuilder reg = new JsonBuilder();
        reg.setDefault(Person.class, PersonViewer.class);
        reg.setDefault(Contact.class, ContactViewer.class);
        //reg.setDefault(User.class, UserAdapter.class);

        Person person = new Person("sergio");
        person.addContact(ContactType.EMAIL, "bla@gmail.com");
        person.addContact(ContactType.MOBILE_PHONE, "845");

        JsonObject expected = new JsonObject();
        expected.addProperty("name", "sergio");
        JsonArray contacts = new JsonArray();
        JsonObject contact = new JsonObject();
        contact.addProperty("type", "EMAIL");
        contact.addProperty("value", "bla@gmail.com");
        contacts.add(contact);
        JsonObject contact2 = new JsonObject();
        contact2.addProperty("type", "MOBILE_PHONE");
        contact2.addProperty("value", "845");
        contacts.add(contact2);
        expected.add("contacts", contacts);
        final JsonElement result = reg.view(person);
        Assert.assertEquals(expected, result);
    }
}
