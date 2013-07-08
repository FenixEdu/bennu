/**
 * 
 */
package pt.ist.bennu.core.rest.json;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt) - 8 de Jul de 2013
 * 
 *         Simple enum viwer that returns the {@link Enum#name()}
 */
@DefaultJsonAdapter(Enum.class)
public class EnumViewer implements JsonViewer<Enum> {


    /* (non-Javadoc)
     * @see pt.ist.bennu.json.JsonViewer#view(java.lang.Object, pt.ist.bennu.json.JsonBuilder)
     */
    @Override
    public JsonElement view(Enum enumObj, JsonBuilder ctx) {
        return enumObj == null ? null : new JsonPrimitive(enumObj.name());
    }

}
