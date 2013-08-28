package pt.ist.bennu.core.rest.json;

import java.math.BigInteger;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonCreator;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

@DefaultJsonAdapter(BigInteger.class)
public class BigIntegerJsonAdapter implements JsonViewer<BigInteger>, JsonCreator<BigInteger> {
    @Override
    public BigInteger create(JsonElement json, JsonBuilder ctx) {
        try {
            return json.getAsBigInteger();
        } catch (NumberFormatException e) {
            throw BennuCoreJsonException.valueIsNotAnInteger(json.toString());
        }
    }

    @Override
    public JsonElement view(BigInteger obj, JsonBuilder ctx) {
        return new JsonPrimitive(obj);
    }
}