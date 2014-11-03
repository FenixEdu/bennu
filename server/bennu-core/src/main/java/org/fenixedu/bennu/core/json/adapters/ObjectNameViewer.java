package org.fenixedu.bennu.core.json.adapters;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@DefaultJsonAdapter(ObjectName.class)
public class ObjectNameViewer implements JsonViewer<ObjectName> {

    @Override
    public JsonElement view(ObjectName obj, JsonBuilder ctx) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            MBeanInfo info = mbs.getMBeanInfo(obj);
            JsonObject json = new JsonObject();

            json.addProperty("description", info.getDescription());
            json.addProperty("className", info.getClassName());

            {
                JsonObject name = new JsonObject();
                name.addProperty("domain", obj.getDomain());
                JsonObject properties = new JsonObject();
                for (Entry<String, String> entry : obj.getKeyPropertyList().entrySet()) {
                    properties.addProperty(entry.getKey(), entry.getValue());
                }
                name.add("properties", properties);

                json.add("name", name);
            }

            {
                JsonArray attributes = new JsonArray();

                for (MBeanAttributeInfo attrInfo : info.getAttributes()) {
                    JsonObject attr = new JsonObject();
                    attr.addProperty("name", attrInfo.getName());
                    attr.addProperty("description", attrInfo.getDescription());
                    attr.addProperty("type", attrInfo.getType());
                    attr.add("value", getValue(obj, mbs, attrInfo));
                    attributes.add(attr);
                }

                json.add("attributes", attributes);
            }

            return json;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private JsonElement getValue(ObjectName obj, MBeanServer mbs, MBeanAttributeInfo attrInfo) {
        try {
            Object value = mbs.getAttribute(obj, attrInfo.getName());
            if (value == null) {
                return null;
            }
            if (value.getClass().isArray()) {
                JsonArray array = new JsonArray();
                for (Object one : (Object[]) value) {
                    array.add(new JsonPrimitive(String.valueOf(one)));
                }
                return array;
            }
            if (value instanceof Map) {
                JsonObject json = new JsonObject();
                for (Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                    json.addProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
                return json;
            }
            if (value instanceof Iterable) {
                JsonArray array = new JsonArray();
                for (Object one : (Iterable<?>) value) {
                    array.add(new JsonPrimitive(String.valueOf(one)));
                }
                return array;
            }
            return new JsonPrimitive(value.toString());
        } catch (Exception e) {
            return new JsonPrimitive("Unavailable: " + e.getMessage());
        }
    }
}
