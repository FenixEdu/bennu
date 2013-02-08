package pt.ist.bennu.json;

public interface JsonCreator<T> {

    T create(String jsonData, JsonBuilder jsonRegistry);
}
