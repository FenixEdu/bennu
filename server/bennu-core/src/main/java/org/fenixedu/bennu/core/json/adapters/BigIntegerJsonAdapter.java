package org.fenixedu.bennu.core.json.adapters;

import java.math.BigInteger;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.BennuCoreJsonException;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonCreator;
import org.fenixedu.bennu.core.json.JsonViewer;

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