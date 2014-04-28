package org.fenixedu.bennu.core.json;

import java.lang.reflect.Type;

import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class Serializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", src.getName());
        return obj;
    }

}

class OtherSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", src.getName());
        obj.addProperty("cenas", "outras cenas");
        return obj;
    }

}

//@RunWith(JUnit4.class)
public class TestGsonRegistry {

    private static GsonBuilder builder;
    private static Person user;

    @BeforeClass
    public static void setup() {
        builder = new GsonBuilder();
        user = new User("test", "testpwd", "1");
        builder.registerTypeAdapter(User.class, new Serializer());
        builder.registerTypeAdapter(User.class, new OtherSerializer());
    }

    //@Test
    public void testGson() {
        final Gson create = builder.create();
    }
}
