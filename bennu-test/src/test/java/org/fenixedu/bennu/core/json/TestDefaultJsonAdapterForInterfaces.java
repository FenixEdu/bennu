package org.fenixedu.bennu.core.json;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RunWith(JUnit4.class)
public class TestDefaultJsonAdapterForInterfaces {

    public static interface Animal {
        String name();
    }

    public static class Dog implements Animal {

        @Override
        public String name() {
            return "It is a dog";
        }

    }

    public static class AnimalAdapter implements JsonViewer<Animal> {

        @Override
        public JsonElement view(Animal animal, JsonBuilder ctx) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", animal.name());
            return obj;
        }
    }

    private static JsonBuilder BUILDER;

    @BeforeClass
    public static void setup() {
        BUILDER = new JsonBuilder();
        BUILDER.setDefault(Animal.class, AnimalAdapter.class);
    }

    @Test
    public void testInterfaces() {
        JsonObject obj = BUILDER.view(new Dog()).getAsJsonObject();
        Assert.assertEquals(new Dog().name(), obj.get("name").getAsString());
    }
}
