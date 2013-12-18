package pt.ist.bennu.core.rest.json;

import java.math.BigInteger;

import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonCreator;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;

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