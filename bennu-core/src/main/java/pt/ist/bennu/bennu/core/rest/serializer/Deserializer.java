package pt.ist.bennu.bennu.core.rest.serializer;

public interface Deserializer {

	public <T> T deserialize(String jsonString, Class<T> type, String externalId);

	public <T> T deserialize(String jsonString, Class<T> type);

	public void updateObject(Object object, String jsonString, String externalId);

}
