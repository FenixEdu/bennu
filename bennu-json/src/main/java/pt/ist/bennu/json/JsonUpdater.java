package pt.ist.bennu.json;

public interface JsonUpdater<T> {
    T update(String jsonData, T obj, JsonBuilder jsonRegistry);
}
